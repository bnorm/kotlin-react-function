@file:Suppress("FunctionName")

package com.bnorm.react

import kotlinx.browser.document
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

fun main() {
  document.getElementById("root")?.let {
    render(it) {
      App()
    }
  }
}

@RFunction
fun RBuilder.App() {
  Counter(initialCount = 10)
}

@RFunction
fun RBuilder.Counter(
  initialCount: Int = 0,
  increaseText: String = "Increase",
  decreaseText: String = "Decrease",
  clearText: String = "Clear"
) {
  var count by useState(initialCount)

  div {
    div {
      +"Count: $count"
    }

    button {
      +increaseText
      attrs.onClickFunction = { count++ }
    }

    button {
      +decreaseText
      attrs.onClickFunction = { count-- }
    }

    button {
      +clearText
      attrs.onClickFunction = { count = 0 }
    }
  }
}
