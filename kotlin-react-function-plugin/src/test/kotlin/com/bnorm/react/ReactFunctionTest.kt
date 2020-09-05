package com.bnorm.react

import com.tschuchort.compiletesting.ExitCode
import com.tschuchort.compiletesting.KotlinJsCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.Test
import kotlin.test.assertEquals

class ReactFunctionTest {
  private fun defaultJsCompilerConfig(): KotlinJsCompilation {
    return KotlinJsCompilation().apply {
      irProduceJs = true
      compilerPlugins = listOf(ReactFunctionComponentRegistrar())
    }
  }

  private val react = SourceFile.kotlin("react.kt", """package react

interface RProps
interface RClass<P : RProps>
typealias RHandler<P> = RElementBuilder<P>.() -> Unit
inline fun <P : RProps> rFunction(displayName: String, crossinline render: RBuilder.(P) -> Unit): RClass<P> = TODO()
class ReactElement
open class RBuilder {
  operator fun <P : RProps> RClass<P>.invoke(handler: RHandler<P>): ReactElement = TODO()
}
open class RElementBuilder<out P : RProps>(open val attrs: P) : RBuilder() {
  fun attrs(handler: P.() -> Unit) {
    attrs.handler()
  }
  
  var key: String = ""
}
""")

  private val comBnormReact = SourceFile.kotlin("com_bnorm_react.kt", """package com.bnorm.react

annotation class RFunction
annotation class RKey
""")

  @Test
  fun `basic compile`() {
    val result = defaultJsCompilerConfig().apply {
      sources = listOf(react, comBnormReact, SourceFile.kotlin("sample.kt", """package test
        
import react.*
import com.bnorm.react.*

@RFunction
fun RBuilder.App() {
  Home("World")
}

@RFunction
fun RBuilder.Home(name: String) {
  println(name)
}

private interface ExpectedHomeProps : RProps {
  var name: String
}

private val EXPECTED_HOME = rFunction<ExpectedHomeProps>("ExpectedHome") { props ->
  println(props.name)
}

fun RBuilder.ExpectedHome(name: String) = EXPECTED_HOME.invoke {
  attrs.name = name
}

"""))
    }.compile()

    assertEquals(result.exitCode, ExitCode.OK)
  }
}
