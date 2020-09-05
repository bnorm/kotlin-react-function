package com.bnorm.react.styles

import kotlinx.css.*
import styled.keyframes

val bg = "#403f4d"
val green = "#62dca5"
val yellow = "#f7f879"
val red = "#e1675a"

val inputColor = "#2e2d33"
val itemColor = "#4e4d5c"
val fontColor = "#fbfafb"

val bigFontSize = 64.px
val headingFontSize = 24.px
val itemFontSize = 20.px

val click = keyframes {
  0 {
    opacity = 0
    width = 0.px
    height = 0.px
  }
  50 {
    opacity = 0.5
  }
  100 {
    opacity = 0
    width = 30.px
    height = 30.px
  }
}
