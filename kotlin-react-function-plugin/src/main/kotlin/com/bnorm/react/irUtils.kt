package com.bnorm.react

import org.jetbrains.kotlin.backend.common.ir.copyTo
import org.jetbrains.kotlin.backend.common.ir.createImplicitParameterDeclarationWithWrappedDescriptor
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.addProperty
import org.jetbrains.kotlin.ir.builders.declarations.addSetter
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

fun IrGeneratorContext.irBuilder(
  symbol: IrSymbol,
  startOffset: Int = UNDEFINED_OFFSET,
  endOffset: Int = UNDEFINED_OFFSET
) = DeclarationIrBuilder(this, symbol, startOffset, endOffset)

fun IrGeneratorContext.buildExternalInterface(
  name: String,
  visibility: Visibility = Visibilities.PUBLIC,
  superTypes: List<IrType>? = listOf(irBuiltIns.anyType)
): IrClass {
  val irClass = buildClass {
    this.visibility = visibility
    kind = ClassKind.INTERFACE
    modality = Modality.ABSTRACT
    isExternal = true
    this.name = Name.identifier(name)
  }
  superTypes?.let { irClass.superTypes = it }
  irClass.createImplicitParameterDeclarationWithWrappedDescriptor()
  return irClass
}

fun IrGeneratorContext.addExternalVarProperty(
  container: IrClass,
  name: Name,
  type: IrType
): IrProperty {
  /*
PROPERTY name:name visibility:public modality:ABSTRACT [var]
  FUN DEFAULT_PROPERTY_ACCESSOR name:<get-name> visibility:public modality:ABSTRACT <> ($this:test.HomeProps) returnType:kotlin.String
    correspondingProperty: PROPERTY name:name visibility:public modality:ABSTRACT [var]
    $this: VALUE_PARAMETER name:<this> type:test.HomeProps
  FUN DEFAULT_PROPERTY_ACCESSOR name:<set-name> visibility:public modality:ABSTRACT <> ($this:test.HomeProps, <set-?>:kotlin.String) returnType:kotlin.Unit
    correspondingProperty: PROPERTY name:name visibility:public modality:ABSTRACT [var]
    $this: VALUE_PARAMETER name:<this> type:test.HomeProps
    VALUE_PARAMETER name:<set-?> index:0 type:kotlin.String
 */

  val irProperty = container.addProperty {
    this.name = name
    modality = Modality.ABSTRACT
    isExternal = true
    isVar = true
  }

  val irGetter = irProperty.addGetter {
    modality = Modality.ABSTRACT
    isExternal = true
    returnType = type
    origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
  }
  irGetter.dispatchReceiverParameter = container.thisReceiver!!.copyTo(irGetter, IrDeclarationOrigin.DEFINED)
  irGetter.correspondingPropertySymbol = irProperty.symbol

  val irSetter = irProperty.addSetter {
    modality = Modality.ABSTRACT
    isExternal = true
    returnType = irBuiltIns.unitType
    origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
  }
  irSetter.dispatchReceiverParameter = container.thisReceiver!!.copyTo(irSetter, IrDeclarationOrigin.DEFINED)
  irSetter.correspondingPropertySymbol = irProperty.symbol
  irSetter.addValueParameter {
    this.name = Name.special("<set-?>")
    this.type = type
  }

  return irProperty
}


fun IrGeneratorContext.buildStaticProperty(parent: IrDeclarationParent, fieldType: IrType, name: String, initializer: DeclarationIrBuilder.() -> IrExpressionBody): IrProperty {
  /*
  PROPERTY name:HOME_RFUNC visibility:private modality:FINAL [val]
    FIELD PROPERTY_BACKING_FIELD name:HOME_RFUNC type:test.RClass<test.HomeFuncProps> visibility:private [final,static]
      EXPRESSION_BODY
        CALL 'public final fun rFunction <P> (displayName: kotlin.String, render: @[ExtensionFunctionType] kotlin.Function2<test.RBuilder, P of test.rFunction, kotlin.Unit>): test.RClass<P of test.rFunction> [inline] declared in test' type=test.RClass<test.HomeFuncProps> origin=null
          <P>: test.HomeFuncProps
          displayName: CONST String type=kotlin.String value="Home"
          render: FUN_EXPR type=@[ExtensionFunctionType] kotlin.Function2<test.RBuilder, test.HomeFuncProps, kotlin.Unit> origin=LAMBDA
            FUN LOCAL_FUNCTION_FOR_LAMBDA name:<anonymous> visibility:local modality:FINAL <> ($receiver:test.RBuilder, props:test.HomeFuncProps) returnType:kotlin.Unit
              $receiver: VALUE_PARAMETER name:<this> type:test.RBuilder
              VALUE_PARAMETER name:props index:0 type:test.HomeFuncProps
              *** BLOCK_BODY ***
    FUN DEFAULT_PROPERTY_ACCESSOR name:<get-HOME_RFUNC> visibility:private modality:FINAL <> () returnType:test.RClass<test.HomeFuncProps>
      correspondingProperty: PROPERTY name:HOME_RFUNC visibility:private modality:FINAL [val]
      BLOCK_BODY
        RETURN type=kotlin.Nothing from='private final fun <get-HOME_RFUNC> (): test.RClass<test.HomeFuncProps> declared in test'
          GET_FIELD 'FIELD PROPERTY_BACKING_FIELD name:HOME_RFUNC type:test.RClass<test.HomeFuncProps> visibility:private [final,static]' type=test.RClass<test.HomeFuncProps> origin=null
 */
  val irProperty = buildProperty {
    this.name = Name.identifier(name)
    visibility = Visibilities.PRIVATE
    modality = Modality.FINAL
  }
  irProperty.parent = parent

  val irGetter = irProperty.addGetter {
    visibility = Visibilities.PRIVATE
    modality = Modality.FINAL
    returnType = fieldType
    origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
  }
  irGetter.correspondingPropertySymbol = irProperty.symbol

  val field = buildField {
    visibility = Visibilities.PRIVATE
    isStatic = true
    isFinal = true
    this.name = Name.identifier(name)
    type = fieldType
    origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
  }
  field.correspondingPropertySymbol = irProperty.symbol
  field.parent = parent
  field.initializer = irBuilder(field.symbol).run { initializer() }

  irGetter.body = irBuilder(irGetter.symbol).run {
    irBlockBody {
      +irReturn(irGetField(null, field))
    }
  }

  irProperty.backingField = field
  return irProperty
}

fun IrBuilderWithScope.buildLambda(returnType: IrType, lambdaType: IrType, builder: IrSimpleFunction.() -> Unit): IrFunctionExpression {
  val scope = this
  val lambda = buildFun {
    name = Name.special("<anonymous>")
    this.returnType = returnType
    visibility = Visibilities.LOCAL
    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
  }.apply {
    builder()
    this.parent = scope.parent
  }
  return IrFunctionExpressionImpl(-1, -1, lambdaType, lambda, IrStatementOrigin.LAMBDA)
}
