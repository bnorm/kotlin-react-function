package com.bnorm.react

import com.bnorm.react.styles.*
import kotlinx.css.*
import styled.StyleSheet

object IndexStyles : StyleSheet("IndexStyles") {
  val global by css {
    body {
      margin(0.px)
      padding(0.px)
      background = bg
      color = Color(fontColor)
      fontFamily = "\"Helvetica\", \"Arial\", sans-serif"
      fontWeight = FontWeight.lighter
      overflowY = Overflow.scroll
      put("-webkit-font-smoothing", "antialiased")
      put("-moz-osx-font-smoothing", "grayscale")

      ".App" {
        position = Position.relative
        margin(40.px, LinearDimension.auto)
        width = 550.px

        media("(max-width: 600px)") {
          width = 95.pct
        }
      }
    }

    "::-webkit-scrollbar" {
      backgroundColor = Color(bg)
      width = 1.em
      media("(max-width: 600px)") {
        width = 0.5.em
      }
    }

    "::-webkit-scrollbar-thumb:window-inactive, ::-webkit-scrollbar-thumb" {
      background = inputColor
      border = "3px solid $bg"
      borderRadius = 3.px
    }
  }
}
