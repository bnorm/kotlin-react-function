@file:Suppress("FunctionName")

package com.bnorm.react

import kotlinx.browser.document
import react.dom.render
import styled.injectGlobal
import com.bnorm.react.IndexStyles as styles

fun main() {
  injectGlobal(styles.global)
  render(document.getElementById("root")) { App() }
}
