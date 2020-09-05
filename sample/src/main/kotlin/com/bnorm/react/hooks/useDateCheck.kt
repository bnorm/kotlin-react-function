package com.bnorm.react.hooks

import com.bnorm.react.*
import kotlinx.browser.window
import kotlinx.datetime.*
import react.useEffectWithCleanup

fun useDateCheck() {
  val date = useAppState().date
  val dispatch = useAppReducer()

  useEffectWithCleanup(listOf(date, dispatch)) {
    val interval  = window.setInterval({
      val now = Clock.System.todayAt(TimeZone.currentSystemDefault())
      if (now > date) {

        /*
        if (remote.getGlobal("notificationSettings").resetNotification) {
          new Notification("todometer reset time!", {
            body: "It's a new day! Your todos are being reset."
          });
        }
         */

        dispatch(AppAction.ResetAll)
        window.location.reload()
      }
    }, 1000)
    return@useEffectWithCleanup { window.clearInterval(interval) }
  }
}
