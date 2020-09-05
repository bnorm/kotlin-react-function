package com.bnorm.react.components

import com.bnorm.react.styles.bigFontSize
import com.bnorm.react.styles.headingFontSize
import kotlinx.css.*
import styled.StyleSheet

object TodoDateStyles : StyleSheet("TodoDateStyles") {
  val date by css {
    display = Display.flex
    alignItems = Align.center
    justifyContent = JustifyContent.spaceBetween
    padding(0.px, 0.px, 40.px, 0.px)
    fontSize = headingFontSize
  }
  val calendar by css {
    display = Display.flex
    alignItems = Align.center
  }
  val day by css {
    fontSize = bigFontSize
    fontWeight = FontWeight.bold
  }
  val my by css {
    padding(0.px, 0.px, 0.px, 5.px)
  }
  val month by css {
    fontWeight = FontWeight.bold
  }
  val year by css {}
  val today by css {}
}
