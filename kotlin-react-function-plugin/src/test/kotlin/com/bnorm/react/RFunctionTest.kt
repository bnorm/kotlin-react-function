package com.bnorm.react

import kotlin.test.assertTrue
import org.junit.Test

class RFunctionTest {
  @Test
  fun welcomeComponent() {
    val output = compile("""
import com.bnorm.react.*
import react.*
import react.dom.div

@RFunction
fun RBuilder.Welcome(name: String) {
  div {
    +"Hello, ${"$"}name"
  }
}
""")
    val javascript = output.readText()
    assertTrue("function Welcome(_this_, name)" in javascript)
    assertTrue("var WELCOME_RFUNC" in javascript)
    assertTrue("WELCOME_RFUNC = rFunction('Welcome'," in javascript)
  }
}
