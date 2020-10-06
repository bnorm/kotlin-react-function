package com.bnorm.react

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinJsCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.File
import kotlin.test.assertEquals
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar

fun compile(
  @Language("kotlin") source: String,
  vararg plugins: ComponentRegistrar = arrayOf(ReactFunctionComponentRegistrar())
): File {
  val result = KotlinJsCompilation().apply {
    sources = listOf(SourceFile.kotlin("main.kt", source, trimIndent = false))
    irProduceJs = true
    irProduceKlibFile = true
    messageOutputStream = System.out
    compilerPlugins = plugins.toList()
    kotlincArguments = listOf(
      "-libraries",
      // Gradle downloads these libraries so they can be included here
      (File("build/jsJars").listFiles()?.toList() ?: emptyList())
        .map { it.absolutePath }.filter { "kotlin-stdlib-common" !in it }.joinToString(File.pathSeparator)
    )
  }.compile()
  assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
  return File(result.outputDirectory, "test.js")
}
