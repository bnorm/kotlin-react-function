@file:Suppress("FunctionName")

package com.bnorm.react

import kotlinx.browser.document
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

fun main() {
  document.getElementById("root")?.let {
    render(it) { Counter(initialCount = 10) }
  }
}

@RFunction
fun RBuilder.Counter(
  initialCount: Int = 0,
  increaseText: String = "Increase"
) {
  var count by useState(initialCount)
  div { +"Count: $count" }
  button {
    +increaseText
    attrs.onClickFunction = { count++ }
  }
}
