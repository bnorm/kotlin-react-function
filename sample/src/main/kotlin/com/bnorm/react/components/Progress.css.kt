package com.bnorm.react.components

import com.bnorm.react.styles.*
import kotlinx.css.*
import kotlinx.css.properties.ms
import kotlinx.css.properties.transition
import styled.StyleSheet

object ProgressStyles : StyleSheet("ProgressStyles") {
  val progress by css {
    position = Position.relative
    width = 100.pct
    height = 15.px
    margin(0.px, 0.px, 20.px)
    background = inputColor
    borderRadius = 3.px
  }
  val progressbar by css {
    position = Position.absolute
    top = 0.px
    width = 100.pct
    height = 100.pct
    borderRadius = 3.px
    transition("width", 500.ms)
  }
  val paused by css {
    background = yellow
  }
  val completed by css {
    background = green
  }
}
