package com.bnorm.react.components

import com.bnorm.react.*
import com.bnorm.react.styles.div
import kotlinx.css.*
import react.RBuilder
import styled.*
import com.bnorm.react.components.ProgressStyles as styles

@RFunction
fun RBuilder.Progress() {
  val totalAmount = useAppState().items.size.toDouble()
  val (_, paused, completed) = useItems()

  val completedPercentage: Double
  val pausedPercentage: Double
  if (totalAmount > 0) {
    completedPercentage = completed.size / totalAmount
    pausedPercentage = paused.size / totalAmount + completedPercentage
  } else {
    completedPercentage = 0.0
    pausedPercentage = 0.0
  }

  div(className = styles.progress) {
    div(className = { +styles.progressbar; +styles.paused; width = (pausedPercentage * 100).pct }) {}
    div(className = { +styles.progressbar; +styles.completed; width = (completedPercentage * 100).pct }) {}
  }
}
