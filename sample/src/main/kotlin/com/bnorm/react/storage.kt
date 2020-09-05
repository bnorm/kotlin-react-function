package com.bnorm.react

import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

private const val KEY = "com.bnorm.react.kotlin-react-function-sample.state"

fun loadState(): AppState {
  try {
    val serializedState = localStorage.getItem(KEY) ?: return AppState()
    return Json.decodeFromString(AppState.serializer(), serializedState)
  } catch (t: Throwable) {
    return AppState()
  }
}

fun saveState(state: AppState) {
  try {
    val serializedState = Json.encodeToString(AppState.serializer(), state)
    localStorage.setItem(KEY, serializedState)
  } catch (t: Throwable) {
    console.error(t)
  }
}
