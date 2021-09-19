@file:Suppress("PropertyName")

package com.bnorm.react

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.symbols.FqNameEqualityChecker
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.FqName

internal class KnownFunctionSymbols(
  context: IrPluginContext,
  types: KnownClassTypes = KnownClassTypes(context)
) {
  val react: ReactPackage = ReactPackage(context, types)
  class ReactPackage(context: IrPluginContext, types: KnownClassTypes) {
    val fc = context.referenceFunctions(FqName("react.fc"))
      .single { it.owner.valueParameters.size == 2 } // TODO match all parameters

    val RBuilder = RBuilderClass(context, types)
    class RBuilderClass(context: IrPluginContext, types: KnownClassTypes) {
      private val functions = types.classes.react.RBuilder.functions.filter { it.isBound }
      val child = run {
        val irElementTypeClassifier = types.react.ElementType.classifier
        functions.asSequence()
          .filter { it.owner.name.asString() == "child" }
          .filter { it.owner.visibility == DescriptorVisibilities.PUBLIC }
          .filter { it.owner.valueParameters.size == 3 }
          .single {
            val firstParameter = it.owner.valueParameters[0].type.classifierOrNull ?: return@single false
            // TODO match all parameters
            FqNameEqualityChecker.areEqual(firstParameter, irElementTypeClassifier)
          }
      }
    }

    val RElementBuilder = RElementBuilderClass(context, types)
    class RElementBuilderClass(context: IrPluginContext, types: KnownClassTypes) {
      private val properties = types.classes.react.RElementBuilder.owner.properties
      val attrs = properties.filter { it.name.asString() == "attrs" }.single().symbol
      val key = properties.filter { it.name.asString() == "key" }.single().symbol
    }
  }

  val kotlin: KotlinPackage = KotlinPackage(context, types)
  class KotlinPackage(context: IrPluginContext, types: KnownClassTypes) {
    val toString = context.referenceFunctions(FqName("kotlin.toString")).single()
  }
}
