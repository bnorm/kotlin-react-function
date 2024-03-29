@file:Suppress("PropertyName", "FunctionName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.createType

internal class KnownClassTypes(
  context: IrPluginContext,
  val classes: KnownClassSymbols = KnownClassSymbols(context)
) {
  val react: ReactPackage = ReactPackage(context, classes)
  class ReactPackage(context: IrPluginContext, private val classes: KnownClassSymbols) {
    val Props = classes.react.Props.createType(false, emptyList())
    fun FC(type: IrType = Props): IrSimpleType =
      classes.react.FC.createType(false, arguments = listOf(type as IrTypeArgument))

    val ElementType = classes.react.ElementType.createType(false, emptyList())
    val RBuilder = classes.react.RBuilder.createType(false, emptyList())
    fun RElementBuilder(type: IrType = Props): IrSimpleType =
      classes.react.RElementBuilder.createType(false, listOf(type as IrTypeArgument))
  }

  val com_bnorm_react: BnormReactPackage = BnormReactPackage(context, classes)
  class BnormReactPackage(context: IrPluginContext, classes: KnownClassSymbols) {
    val RFunction = classes.com_bnorm_react.RFunction.createType(false, emptyList())
    val RKey = classes.com_bnorm_react.RKey.createType(false, emptyList())
  }
}

