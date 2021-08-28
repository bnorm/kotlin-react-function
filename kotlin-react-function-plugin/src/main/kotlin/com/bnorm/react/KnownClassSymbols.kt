@file:Suppress("PropertyName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.name.FqName

internal class KnownClassSymbols(context: IrPluginContext) {
  val react: ReactPackage = ReactPackage(context)
  class ReactPackage(context: IrPluginContext) {
    val FC = context.referenceTypeAlias(FqName("react.FC"))!!
    val Props = context.referenceClass(FqName("react.Props"))!!
    val ElementType = context.referenceClass(FqName("react.ElementType"))!!
    val RBuilder = context.referenceClass(FqName("react.RBuilder"))!!
    val RElementBuilder = context.referenceClass(FqName("react.RElementBuilder"))!!
  }

  val com_bnorm_react: BnormReactPackage = BnormReactPackage(context)
  class BnormReactPackage(context: IrPluginContext) {
    val RFunction = context.referenceClass(FqName("com.bnorm.react.RFunction"))!!
    val RKey = context.referenceClass(FqName("com.bnorm.react.RKey"))!!
  }
}
