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
}
""")

  @Test
  fun `before`() {
    val result = defaultJsCompilerConfig().apply {
      sources = listOf(react, SourceFile.kotlin("sample.kt", """package test
        
import react.*

annotation class RFunction

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
    println(result.outputDirectory)
  }
}

/*
  PROPERTY name:HOME_RFUNC visibility:private modality:FINAL [val]
    FIELD PROPERTY_BACKING_FIELD name:HOME_RFUNC type:test.RClass<test.HomeFuncProps> visibility:private [final,static]
      EXPRESSION_BODY
        CALL 'public final fun rFunction <P> (displayName: kotlin.String, render: @[ExtensionFunctionType] kotlin.Function2<test.RBuilder, P of test.rFunction, kotlin.Unit>): test.RClass<P of test.rFunction> [inline] declared in test' type=test.RClass<test.HomeFuncProps> origin=null
          <P>: test.HomeFuncProps
          displayName: CONST String type=kotlin.String value="Home"
          render: FUN_EXPR type=kotlin.Unit origin=LAMBDA
            FUN LOCAL_FUNCTION_FOR_LAMBDA name:<anonymous> visibility:local modality:FINAL <> ($receiver:test.RBuilder, props:test.HomeFuncProps) returnType:kotlin.Unit
              $receiver: VALUE_PARAMETER name:$receiver type:test.RBuilder
              VALUE_PARAMETER name:props index:0 type:test.HomeFuncProps
              BLOCK_BODY
                CALL 'public final fun println (message: kotlin.Any?): kotlin.Unit declared in kotlin.io' type=kotlin.Unit origin=null
                  message: CALL 'public abstract fun <get-name> (): kotlin.String declared in test.HomeFuncProps' type=kotlin.String origin=null
                    $this: GET_VAR 'props: test.HomeFuncProps declared in test.HOME_RFUNC.<anonymous>' type=test.HomeFuncProps origin=null
    FUN DEFAULT_PROPERTY_ACCESSOR name:<get-HOME_RFUNC> visibility:private modality:FINAL <> () returnType:test.RClass<test.HomeFuncProps>
      correspondingProperty: PROPERTY name:HOME_RFUNC visibility:private modality:FINAL [val]
      BLOCK_BODY
        RETURN type=kotlin.Nothing from='private final fun <get-HOME_RFUNC> (): test.RClass<test.HomeFuncProps> declared in test'
          GET_FIELD 'FIELD PROPERTY_BACKING_FIELD name:HOME_RFUNC type:test.RClass<test.HomeFuncProps> visibility:private [final,static]' type=test.RClass<test.HomeFuncProps> origin=null
 */
