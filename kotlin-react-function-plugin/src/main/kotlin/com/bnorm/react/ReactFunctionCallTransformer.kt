/*
 * Copyright (C) 2020 Brian Norman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.setDeclarationsParent
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.declarations.addExtensionReceiver
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrBodyBase
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.Name
import java.io.File

fun FileLoweringPass.runOnFileInOrder(irFile: IrFile) {
  irFile.acceptVoid(object : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) {
      element.acceptChildrenVoid(this)
    }

    override fun visitFile(declaration: IrFile) {
      lower(declaration)
      super.visitFile(declaration)
    }
  })
}

class ReactFunctionCallTransformer(
  private val context: IrPluginContext,
  private val messageCollector: MessageCollector,
) : IrElementTransformerVoidWithContext(), FileLoweringPass {
  private val classes = KnownClassTypes(context)
  private val functions = KnownFunctionSymbols(context, classes)

  private lateinit var file: IrFile
  private lateinit var fileSource: String

  private val newDeclarations = mutableListOf<IrDeclaration>()

  override fun lower(irFile: IrFile) {
    file = irFile
    fileSource = File(irFile.path).readText()

    println(file.dump())
    irFile.transformChildrenVoid()
    irFile.declarations.addAll(newDeclarations.filter { it.parent == irFile })
  }

  override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
    if (validateSignature(declaration)) {
      transformFunction(declaration)
    }
    return super.visitSimpleFunction(declaration)
  }

  private fun validateSignature(declaration: IrSimpleFunction): Boolean {
    if (declaration.annotations.none { it.type == classes.com_bnorm_react.RFunction }) return false

    val location by lazy {
      val line = fileSource.substring(declaration.startOffset).count { it == '\n' } + 1
      println(line)
      CompilerMessageLocation.create(file.path, line, -1, null)
    }

    var result = true
    if (declaration.parent !is IrFile) {
      messageCollector.report(CompilerMessageSeverity.ERROR, "RFunction annotated function must be a top-level function", location)
      result = false
    }

    if (declaration.extensionReceiverParameter?.type != classes.react.RBuilder) {
      messageCollector.report(CompilerMessageSeverity.ERROR, "RFunction annotated function must be an extension function of react.RBuilder", location)
      result = false
    }

    if (declaration.returnType != context.irBuiltIns.unitType) {
      messageCollector.report(CompilerMessageSeverity.ERROR, "RFunction annotated function must return Unit", location)
      result = false
    }

    return result
  }

  private fun transformFunction(declaration: IrSimpleFunction) {
    val body = (declaration.body as? IrBlockBody) ?: return
    val parent = declaration.parent as IrDeclarationContainer

    val props = context.buildPropsInterface(declaration)
    props.parent = parent
    newDeclarations.add(props)

    val component = buildRFunctionProperty(parent, declaration, props, body)
    newDeclarations.add(component)

    declaration.body = buildNewBody(props, component, declaration)
  }

  private fun IrGeneratorContext.buildPropsInterface(declaration: IrSimpleFunction): IrClass {
    val irClass = buildExternalInterface(
      name = "${declaration.name}FuncProps",
      visibility = Visibilities.PRIVATE,
      superTypes = listOf(classes.react.RProps)
    )
    for (valueParameter in declaration.valueParameters) {
      addExternalVarProperty(
        container = irClass,
        name = valueParameter.name,
        type = valueParameter.type
      )
    }
    return irClass
  }

  private fun buildRFunctionProperty(parent: IrDeclarationParent, declaration: IrSimpleFunction, propsClass: IrClass, body: IrBlockBody): IrProperty {
    val fieldType = classes.react.RClass(propsClass.defaultType)
    val name = "${declaration.name}_RFUNC".toUpperCase()

    return context.buildStaticProperty(parent, fieldType, name) {
      irExprBody(irCall_rFunction(propsClass.defaultType, "${declaration.name}") { function ->
        val rBuilder = function.extensionReceiverParameter!!
        val props = function.valueParameters[0]
        for (statement in body.statements) {
          +statement.transform(object : IrElementTransformerVoid() {
            override fun visitGetValue(expression: IrGetValue): IrExpression {

              /*
               * Replace all IrGetValues of dispatch receiver and function
               * parameters with the correct calls relative to rFunction
               * builder.
               */

              val owner = expression.symbol.owner
              if (owner.parent == declaration) {
                val property = propsClass.declarations
                  .filterIsInstance<IrProperty>()
                  .singleOrNull { it.name == owner.name }
                if (property != null) {
                  return context.irBuilder(expression.symbol).run {
                    irCall(property.getter!!, origin = IrStatementOrigin.GET_PROPERTY).apply {
                      dispatchReceiver = irGet(props)
                    }
                  }
                } else if (owner.name == Name.special("<this>") && owner.parent == (body as IrBodyBase<*>).container) {
                  return context.irBuilder(expression.symbol).run {
                    irGet(rBuilder)
                  }
                }
              }

              return super.visitGetValue(expression)
            }
          }, null).setDeclarationsParent(function)
        }
      })
    }
  }

  // TODO better name?
  private fun IrBuilderWithScope.irCall_rFunction(propsType: IrType, name: String, body: IrBlockBodyBuilder.(IrSimpleFunction) -> Unit): IrCall {
    /*
CALL 'public final fun rFunction <P> (displayName: kotlin.String, render: @[ExtensionFunctionType] kotlin.Function2<test.RBuilder, P of test.rFunction, kotlin.Unit>): test.RClass<P of test.rFunction> [inline] declared in test' type=test.RClass<test.ExpectedHomeProps> origin=null
  <P>: test.ExpectedHomeProps
  displayName: CONST String type=kotlin.String value="ExpectedHome"
  render: FUN_EXPR type=@[ExtensionFunctionType] kotlin.Function2<test.RBuilder, test.ExpectedHomeProps, kotlin.Unit> origin=LAMBDA
    FUN LOCAL_FUNCTION_FOR_LAMBDA name:<anonymous> visibility:local modality:FINAL <> ($receiver:test.RBuilder, props:test.ExpectedHomeProps) returnType:kotlin.Unit
      $receiver: VALUE_PARAMETER name:<this> type:test.RBuilder
      VALUE_PARAMETER name:props index:0 type:test.ExpectedHomeProps
      BLOCK_BODY
     */

    val rClassType = classes.react.RClass(propsType)

    // TODO type=@[ExtensionFunctionType]?
    val lambdaType = context.irBuiltIns.function(2)
      .createType(false, listOf(
        classes.react.RBuilder as IrTypeArgument,
        propsType as IrTypeArgument,
        context.irBuiltIns.unitType as IrTypeArgument
      ))

    return irCall(functions.react.rFunction, rClassType).apply {
      putTypeArgument(0, propsType)
      putValueArgument(0, irString(name))
      putValueArgument(1, buildLambda(context.irBuiltIns.unitType, lambdaType) {
        val function = this
        // TODO currently = $receiver: VALUE_PARAMETER name:$receiver type:test.RBuilder
        addExtensionReceiver { type = classes.react.RBuilder }
        addValueParameter("props", propsType)
        this.body = context.irBuilder(symbol).irBlockBody { body(function) }
      })
    }
  }

  private fun buildNewBody(propsClass: IrClass, componentProperty: IrProperty, declaration: IrSimpleFunction): IrBody {
    return context.irBuilder(declaration.symbol).run {
      irBlockBody {
        +irCall_invoke(
          propsClass.defaultType,
          irGet(declaration.extensionReceiverParameter!!),
          irCall(componentProperty.getter!!, origin = IrStatementOrigin.GET_PROPERTY)
        ) { function ->
          /*
BLOCK_BODY
  CALL 'public abstract fun <set-name> (<set-?>: kotlin.String): kotlin.Unit declared in test.ExpectedHomeProps' type=kotlin.Unit origin=EQ
    $this: CALL 'public open fun <get-attrs> (): P of test.RElementBuilder declared in test.RElementBuilder' type=test.ExpectedHomeProps origin=GET_PROPERTY
      $this: GET_VAR '<this>: test.RElementBuilder<test.ExpectedHomeProps> declared in test.ExpectedHome.<anonymous>' type=test.RElementBuilder<test.ExpectedHomeProps> origin=null
    <set-?>: GET_VAR 'name: kotlin.String declared in test.ExpectedHome' type=kotlin.String origin=null
           */
          val rElementBuilder = function.extensionReceiverParameter!!
          val properties = propsClass.declarations.filterIsInstance<IrProperty>().associateBy { it.name }

          for (valueParameter in declaration.valueParameters) {
            val property = properties.getValue(valueParameter.name)

            +irCall(property.setter!!, origin = IrStatementOrigin.EQ).apply {
              val callee = functions.react.RElementBuilder.attrs.owner.getter!!
              this.dispatchReceiver = IrCallImpl(startOffset, endOffset, propsClass.defaultType, callee.symbol, callee.typeParameters.size, callee.valueParameters.size, IrStatementOrigin.GET_PROPERTY).apply {
                this.dispatchReceiver = irGet(rElementBuilder)
              }
              this.putValueArgument(0, irGet(valueParameter))
            }
          }
        }
      }
    }
  }

  // TODO better name?
  private fun IrBuilderWithScope.irCall_invoke(propsType: IrType, dispatchReceiver: IrExpression, extensionReceiver: IrExpression, body: IrBlockBodyBuilder.(IrSimpleFunction) -> Unit): IrCall {
    /*
CALL 'public final fun invoke <P> (handler: @[ExtensionFunctionType] kotlin.Function1<test.RElementBuilder<P of test.RBuilder.invoke>, kotlin.Unit>{ test.RHandler<P of test.RBuilder.invoke> }): test.ReactElement [operator] declared in test.RBuilder' type=test.ReactElement origin=null
  <P>: test.ExpectedHomeProps
  $this: GET_VAR '<this>: test.RBuilder declared in test.ExpectedHome' type=test.RBuilder origin=null
  $receiver: CALL 'private final fun <get-EXPECTED_HOME> (): test.RClass<test.ExpectedHomeProps> declared in test' type=test.RClass<test.ExpectedHomeProps> origin=GET_PROPERTY
  handler: FUN_EXPR type=@[ExtensionFunctionType] kotlin.Function1<test.RElementBuilder<test.ExpectedHomeProps>, kotlin.Unit> origin=LAMBDA
    FUN LOCAL_FUNCTION_FOR_LAMBDA name:<anonymous> visibility:local modality:FINAL <> ($receiver:test.RElementBuilder<test.ExpectedHomeProps>) returnType:kotlin.Unit
      $receiver: VALUE_PARAMETER name:<this> type:test.RElementBuilder<test.ExpectedHomeProps>
      BLOCK_BODY
     */

    // TODO type=@[ExtensionFunctionType]?
    val typeRElementBuilder = classes.react.RElementBuilder(propsType)
    val lambdaType = context.irBuiltIns.function(1)
      .createType(false, listOf(
        typeRElementBuilder as IrTypeArgument,
        context.irBuiltIns.unitType as IrTypeArgument
      ))

    return irCall(functions.react.RBuilder.invoke, classes.react.ReactElement).apply {
      putTypeArgument(0, propsType)
      this.dispatchReceiver = dispatchReceiver
      this.extensionReceiver = extensionReceiver
      putValueArgument(0, buildLambda(context.irBuiltIns.unitType, lambdaType) {
        val function = this
        addExtensionReceiver { this.type = typeRElementBuilder }
        this.body = context.irBuilder(symbol).irBlockBody { body(function) }
      })
    }
  }
}
