package com.bnorm.react

import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

fun loadState(): AppState {
  try {
    val serializedState = localStorage.getItem("state") ?: return AppState()
    return Json.decodeFromString(AppState.serializer(), serializedState)
  } catch (t: Throwable) {
    return AppState()
  }
}

fun saveState(state: AppState) {
  try {
    val serializedState = Json.encodeToString(AppState.serializer(), state)
    localStorage.setItem("state", serializedState)
  } catch (t: Throwable) {
    console.error(t)
  }
}
