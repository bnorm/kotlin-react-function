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
    assertTrue("WELCOME_RFUNC = functionalComponent('Welcome'," in javascript)
  }

  @Test
  fun genericComponent() {
    val output = compile("""
import com.bnorm.react.*
import react.*
import react.dom.div

@RFunction
fun <T> RBuilder.GenericList(items: List<T>, onItem: RBuilder.(T) -> Unit) {
  div {
    for (item in items) {
      onItem(item)
    }
  }
}
""")
    val javascript = output.readText()
    assertTrue("function GenericList(_this_, items, onItem)" in javascript)
    assertTrue("var GENERICLIST_RFUNC" in javascript)
    assertTrue("GENERICLIST_RFUNC = functionalComponent('GenericList'," in javascript)
  }
}
