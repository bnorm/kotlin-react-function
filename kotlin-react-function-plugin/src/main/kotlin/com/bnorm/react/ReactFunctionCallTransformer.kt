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

import java.io.File
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.setDeclarationsParent
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
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
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.name.Name

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
    if (declaration.annotations.none { it.type == classes.com_bnorm_react.RFunction }) {
      val first = declaration.valueParameters.firstOrNull { parameter -> parameter.annotations.any { it.type == classes.com_bnorm_react.RKey } }
      if (first != null) {
        val line = fileSource.substring(first.startOffset).count { it == '\n' } + 1
        val location = CompilerMessageLocation.create(file.path, line, -1, null)
        messageCollector.report(CompilerMessageSeverity.ERROR, "RKey may only be used on function annotated with RFunction", location)
      }

      return false
    }

    val location by lazy {
      val line = fileSource.substring(declaration.startOffset).count { it == '\n' } + 1
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

    val keys = declaration.valueParameters.count { it.annotations.any { it.type == classes.com_bnorm_react.RKey } }
    if (keys > 1) {
      messageCollector.report(CompilerMessageSeverity.ERROR, "RKey may only be applied to a single parameter", location)
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
      visibility = DescriptorVisibilities.PRIVATE,
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
          val transformed = statement.transform(object : IrElementTransformerVoid() {
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
                } else if (owner.name == Name.special("<this>") &&
                  expression.type == classes.react.RBuilder
                ) {
                  return context.irBuilder(expression.symbol).run {
                    irGet(rBuilder)
                  }
                }
              }

              return super.visitGetValue(expression)
            }
          }, null)
          transformed.setDeclarationsParent(function)
          +(transformed as IrStatement)
        }
      })
    }
  }

  // TODO better name?
  private fun IrBuilderWithScope.irCall_rFunction(propsType: IrType, name: String, body: IrBlockBodyBuilder.(IrSimpleFunction) -> Unit): IrCall {
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
        addExtensionReceiver(type = classes.react.RBuilder)
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

          val keyParameter = declaration.valueParameters.singleOrNull { parameter ->
            parameter.annotations.any { it.type == classes.com_bnorm_react.RKey }
          }
          if (keyParameter != null) {
            +irCall(functions.react.RElementBuilder.key.owner.setter!!, origin = IrStatementOrigin.EQ).apply {
              this.dispatchReceiver = irGet(rElementBuilder)
              this.putValueArgument(0, irCall(functions.kotlin.toString).apply {
                this.extensionReceiver = irGet(keyParameter)
              })
            }
          }
        }
      }
    }
  }

  // TODO better name?
  private fun IrBuilderWithScope.irCall_invoke(propsType: IrType, dispatchReceiver: IrExpression, extensionReceiver: IrExpression, body: IrBlockBodyBuilder.(IrSimpleFunction) -> Unit): IrCall {
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
        addExtensionReceiver(type = typeRElementBuilder)
        this.body = context.irBuilder(symbol).irBlockBody { body(function) }
      })
    }
  }
}
