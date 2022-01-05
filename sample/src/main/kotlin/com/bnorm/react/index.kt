@file:Suppress("FunctionName")

package com.bnorm.react

import kotlinx.browser.document
import react.buildElement
import react.dom.render
import styled.injectGlobal
import com.bnorm.react.IndexStyles as styles

fun main() {
  injectGlobal(styles.global)

  val container = document.createElement("div")
  document.body!!.appendChild(container)

  render(buildElement { App() }, container)
}
