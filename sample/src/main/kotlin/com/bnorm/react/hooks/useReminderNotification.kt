package com.bnorm.react.hooks

import com.bnorm.react.useItems
import kotlinx.browser.window
import kotlinx.datetime.*
import react.useEffectWithCleanup

fun getTimeCondition(date: LocalDate): Boolean {
  var condition = false

/*
  switch (remote.getGlobal("notificationSettings").reminderNotification) {
    case "hour":
      condition = nd.getMinutes() === 0 && nd.getSeconds() === 0;
      break;
    case "halfhour":
      condition = nd.getMinutes() % 30 === 0 && nd.getSeconds() === 0;
      break;
    case "quarterhour":
      condition = nd.getMinutes() % 15 === 0 && nd.getSeconds() === 0;
      break;
    default:
      break;
  }
*/

  return condition
}

fun useReminderNotification() {
  val (pending, paused) = useItems()

  useEffectWithCleanup(listOf(pending.size, paused.size)) {
    val internal = window.setInterval({
      val now = Clock.System.todayAt(TimeZone.currentSystemDefault())

      if (getTimeCondition(now) && (pending.isNotEmpty() || paused.isNotEmpty())) {
        /*
        let text = `Don't forget, you have ${pending.length +
          paused.length} tasks to do today (${pending.length} incomplete, ${
          paused.length
        } paused for later)`;

        new Notification("todometer reminder!", {
          body: text
        });
         */
      }
    }, 1000)
    return@useEffectWithCleanup { window.clearInterval(internal)}
  }
}
