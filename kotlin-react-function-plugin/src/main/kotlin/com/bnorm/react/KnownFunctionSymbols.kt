@file:Suppress("PropertyName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.name.FqName

internal class KnownFunctionSymbols(context: IrPluginContext, types: KnownClassTypes = KnownClassTypes(context)) {
  val react: ReactPackage = ReactPackage(context, types)
  class ReactPackage(context: IrPluginContext, types: KnownClassTypes) {
    val rFunction = context.referenceFunctions(FqName("react.rFunction")).single() // TODO proper filter

    val RBuilder = RBuilderClass(context, types)
    class RBuilderClass(context: IrPluginContext, types: KnownClassTypes) {
      private val irRClassClassifier = types.react.RClass().classifier
      val invoke = context.referenceFunctions(FqName("react.RBuilder.invoke")).single {
        // TODO proper filter
        val left = (it.owner.extensionReceiverParameter?.type as? IrSimpleType)?.classifier ?: return@single false
        FqNameEqualityChecker.areEqual(left, irRClassClassifier)
      }
    }

    val RElementBuilder = RElementBuilderClass(context, types)
    class RElementBuilderClass(context: IrPluginContext, types: KnownClassTypes) {
      val attrs = context.referenceProperties(FqName("react.RElementBuilder.attrs")).single() // TODO proper filter
      val key = context.referenceProperties(FqName("react.RElementBuilder.key")).single() // TODO proper filter
    }
  }

  val kotlin: KotlinPackage = KotlinPackage(context, types)
  class KotlinPackage(context: IrPluginContext, types: KnownClassTypes) {
    val toString = context.referenceFunctions(FqName("kotlin.toString")).single() // TODO proper filter
  }
}
