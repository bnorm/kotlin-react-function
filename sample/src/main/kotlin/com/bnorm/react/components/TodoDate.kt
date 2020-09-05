package com.bnorm.react.components

import com.bnorm.react.*
import com.bnorm.react.hooks.useDateCheck
import com.bnorm.react.hooks.useReminderNotification
import com.bnorm.react.styles.div
import react.RBuilder
import com.bnorm.react.components.TodoDateStyles as styles

private val WEEKDAYS = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

@RFunction
fun RBuilder.TodoDate() {
  val date = useAppState().date

  useDateCheck()
  useReminderNotification()

  div(className = styles.date) {
    div(className = styles.calendar) {
      div(className = styles.day) { +date.dayOfMonth.toString() }
      div(className = styles.my) {
        div(className = styles.month) { +MONTHS[date.month.ordinal] }
        div(className = styles.year) { +date.year.toString() }
      }
    }
    div(className = styles.today) { +WEEKDAYS[date.dayOfWeek.ordinal] }
  }
}
