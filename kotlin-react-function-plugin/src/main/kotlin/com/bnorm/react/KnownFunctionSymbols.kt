@file:Suppress("PropertyName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.name.FqName

internal class KnownFunctionSymbols(context: IrPluginContext, types: KnownClassTypes = KnownClassTypes(context)) {
  val react: ReactPackage = ReactPackage(context, types)
  class ReactPackage(context: IrPluginContext, types: KnownClassTypes) {
    val fc = context.referenceFunctions(FqName("react.fc"))
      .single { it.owner.valueParameters.size == 2 } // TODO proper filter

    val RBuilder = RBuilderClass(context, types)
    class RBuilderClass(context: IrPluginContext, types: KnownClassTypes) {
      val invoke = run {
        val irElementTypeClassifier = types.react.ElementType.classifier
        val possible = context.referenceFunctions(FqName("react.RBuilder.invoke"))
        possible.asSequence()
          .filter { it.isBound }
          .filter { it.owner.valueParameters.size == 1 }
          .single {
            val extensionReceiverParameter = it.owner.extensionReceiverParameter
            val extensionReceiver = extensionReceiverParameter?.type?.classifierOrNull
            extensionReceiver != null && FqNameEqualityChecker.areEqual(extensionReceiver, irElementTypeClassifier)
          }
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
