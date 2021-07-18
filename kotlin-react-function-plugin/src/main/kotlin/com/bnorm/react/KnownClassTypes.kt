@file:Suppress("PropertyName", "FunctionName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.impl.buildSimpleType

internal class KnownClassTypes(context: IrPluginContext, classes: KnownClassSymbols = KnownClassSymbols(context)) {
  val react: ReactPackage = ReactPackage(context, classes)
  class ReactPackage(context: IrPluginContext, private val classes: KnownClassSymbols) {
    val RProps = classes.react.RProps.createType(false, emptyList())
    fun FC(type: IrType = RProps): IrSimpleType {
      val typeAlias = classes.react.FC.owner.expandedType as IrSimpleType
      return typeAlias.buildSimpleType {
        arguments = listOf(type as IrTypeArgument)
      }
    }

    val ReactElement = classes.react.ReactElement.createType(false, emptyList())
    val RBuilder = classes.react.RBuilder.createType(false, emptyList())
    fun RElementBuilder(type: IrType = RProps): IrSimpleType =
      classes.react.RElementBuilder.createType(false, listOf(type as IrTypeArgument))
  }

  val com_bnorm_react: BnormReactPackage = BnormReactPackage(context, classes)
  class BnormReactPackage(context: IrPluginContext, classes: KnownClassSymbols) {
    val RFunction = classes.com_bnorm_react.RFunction.createType(false, emptyList())
    val RKey = classes.com_bnorm_react.RKey.createType(false, emptyList())
  }
}

