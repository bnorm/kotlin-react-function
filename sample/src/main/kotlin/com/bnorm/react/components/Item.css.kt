package com.bnorm.react.components

import com.bnorm.react.styles.*
import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object ItemStyles : StyleSheet("ItemStyles") {
  val item by css {
    display = Display.flex
    alignItems = Align.center
    justifyContent = JustifyContent.spaceBetween
    margin(0.px, 0.px, 10.px, 0.px)
    padding(20.px)
    width = 100.pct
    minHeight = 70.px
    background = itemColor
    border = "none"
    borderRadius = 3.px
    boxSizing = BoxSizing.borderBox
    fontSize = itemFontSize
    media("(max-width: 600px)") {
      margin(20.px, LinearDimension.auto)
    }
  }

  val itemName by css {
    put("width", "calc(100% - 110px)")
    overflow = Overflow.auto

    /*
    &::-webkit-scrollbar {
      background-color: $item-color;
      height: 0.75em;
      @media (max-width: 600px) {
        height: 0.25em;
      }
    }

    &::-webkit-scrollbar-thumb:window-inactive,
    &::-webkit-scrollbar-thumb {
      background: $bg;
      border: 3px solid $item-color;
      border-left: none;
      border-right: none;
      border-radius: 3px;
    }
     */
  }

  val buttons by css {
    display = Display.flex
    justifyContent = JustifyContent.spaceBetween
    width = 100.px

    button {
      position = Position.relative
      height = 24.px
      border = "none"
      after {
        display = Display.block
        content = QuotedString("")
        position = Position.absolute
        top = 50.pct
        left = 50.pct
        transform { translate((-50).pct, (-50).pct) }
        borderRadius = 100.pct
        width = 0.px
        height = 0.px
      }
      focus {
        put("outline", "1px solid $itemColor")
        after {
          animation(click, 0.5.s)
        }
      }
      hover {
        after {
          animation(click, 0.5.s)
        }
      }
    }
  }

  val completedButtons by css {
    justifyContent = JustifyContent.flexEnd
  }

  val delete by css {
    width = 24.px
    background = "no-repeat url(\"img/x.svg\")"
    after {
      background = red
    }
  }

  val pause by css {
    width = 24.px
    background = "no-repeat url(\"img/pause.svg\")"
    after {
      background = yellow
    }
  }
  val resume by css {
    width = 24.px
    background = "no-repeat url(\"img/resume.svg\")"
    after {
      background = yellow
    }
  }
  val complete by css {
    width = 30.px
    background = "no-repeat url(\"img/check.svg\")"
    after {
      background = green
    }
  }
}
