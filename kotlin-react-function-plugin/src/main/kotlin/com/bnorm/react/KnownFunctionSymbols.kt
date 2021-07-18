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
      private val irFcClassifier = types.react.FC().classifier
      private val irRBuilderClassifier = types.react.RBuilder.classifier

      val child = context.referenceFunctions(FqName("react.child")).single {
        // TODO proper filter
        if (it.owner.valueParameters.size != 3) return@single false
        val extensionReceiverParameter = it.owner.extensionReceiverParameter ?: return@single false
        val extensionReceiver = extensionReceiverParameter.type.classifierOrNull ?: return@single false
        val firstParameter = it.owner.valueParameters[0].type.classifierOrNull ?: return@single false

        FqNameEqualityChecker.areEqual(extensionReceiver, irRBuilderClassifier) &&
          FqNameEqualityChecker.areEqual(firstParameter, irFcClassifier)
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
