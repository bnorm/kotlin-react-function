package com.bnorm.react.components

import com.bnorm.react.styles.*
import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet
import styled.keyframes

object ItemListStyles : StyleSheet("ItemListStyles") {
  val slidedown = keyframes {
    0 {
      opacity = 0
      transform { translateY((-10).px) }
    }
    100 {
      opacity = 1
      transform { translateY(0.px) }
    }
  }

  val blink = keyframes {
    0 {
      put("text-shadow", "0 0 2px white")
    }
    50 {
      put("text-shadow", "0 0 10px white")
    }
    100 {
      put("text-shadow", "0 0 2px white")
    }
  }

  val alldone by css {
    display = Display.flex
    justifyContent = JustifyContent.center
    img {
      margin(20.px)
      width = 100.px
      height = 100.px
      animation(slidedown, 1.s)
    }
  }
  val toggle by css {
    display = Display.flex
    alignItems = Align.center
    margin(1.em, 0.px, 0.5.em)
    padding(0.px)
    border = "none"
    background = bg
    color = Color(fontColor)
    cursor = Cursor.pointer
    fontSize = 1.5.em
    fontWeight = FontWeight.bold
    animation(slidedown, 1.s)
    img {
      padding(0.px, 5.px, 0.px, 0.px)
      width = 1.em
      height = 1.em
      boxSizing = BoxSizing.borderBox
      transition("transform", 0.2.s)
    }
    "&[data-state=\"open\"] img" {
      padding(5.px, 0.px, 0.px, 0.px)
      transform { rotate(90.deg) }
    }
    focus {
      put("outline", "2px solid $bg")
      span {
        animation(blink, 0.5.s)
      }
    }
    hover {
      opacity = 0.9
    }
  }
  val panel by css {
    animation(slidedown, 0.2.s, Timing.ease)
  }
  val reset by css {
    textAlign = TextAlign.center
    button {
      background = "transparent"
      border = "none"
      color = Color(fontColor)
      cursor = Cursor.pointer
      fontSize = itemFontSize
      animation(slidedown, 1.s)
      hover {
        opacity = 0.9
      }
    }
  }
}
