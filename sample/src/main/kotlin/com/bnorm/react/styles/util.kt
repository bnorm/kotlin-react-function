package com.bnorm.react.styles

import kotlinx.css.RuleSet
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.dom.RDOMBuilder
import styled.*

fun RBuilder.div(className: RuleSet, block: RDOMBuilder<DIV>.() -> Unit) {
  styledDiv {
    css(className)
    block()
  }
}

fun RBuilder.button(className: RuleSet, onClick: () -> Unit, tabIndex: String) {
  styledButton {
    css(className)
    attrs.onClickFunction = { onClick() }
    attrs.tabIndex = tabIndex
  }
}


fun RBuilder.form(className: RuleSet, block: RDOMBuilder<FORM>.() -> Unit) {
  styledForm {
    css(className)
    block()
  }
}
