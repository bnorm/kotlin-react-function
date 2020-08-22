package com.bnorm.react

import react.dom.render
import kotlinx.browser.document
import react.*
import test.RFunction

fun main() {
  document.getElementById("root")?.let {
    render(it) {
      App()
    }
  }
}

@Suppress("FunctionName")
@RFunction
fun RBuilder.App() {
  +"Hello, World"
}

//@Suppress("FunctionName")
//@RFunction
//fun RBuilder.Home(name: String) {
//  +"Hello, $name"
//}


//@Suppress("FunctionName")
//fun RBuilder.App() {
//  APP_RFUNC.invoke {
//  }
//}
//
//interface AppFuncProps : RProps
//
//val APP_RFUNC = rFunction<AppFuncProps>("App") { props ->
//  +"Hello, World"
//}

