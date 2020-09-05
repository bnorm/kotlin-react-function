package com.bnorm.react.components

import com.bnorm.react.styles.*
import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object AddItemFormStyles : StyleSheet("AddItemFormStyles") {
  val form by css {
    position = Position.relative
    input {
      margin(0.px, 0.px, 10.px, 0.px)
      padding(20.px, 70.px, 20.px, 20.px)
      width = 100.pct
      height = 70.px
      background = inputColor
      border = "none"
      borderRadius = 3.px
      boxSizing = BoxSizing.borderBox
      color = Color(fontColor)
      fontSize = itemFontSize
      outline = Outline.none
    }
    button {
      position = Position.absolute
      top = 20.px
      right = 20.px
      width = 30.px
      height = 30.px
      background = "no-repeat url(\"img/plus.svg\")"
      border = "none"
      after {
        display = Display.block
        content = QuotedString("")
        position = Position.absolute
        top = 50.pct
        left = 50.pct
        background = "#fff"
        transform { translate((-50).pct, (-50).pct) }
        borderRadius = 100.pct
        width = 0.px
        height = 0.px
      }
      focus {
        outline = Outline.none
      }
      hover {
        after {
          animation(click, 0.5.s)
        }
      }
    }
  }
}
