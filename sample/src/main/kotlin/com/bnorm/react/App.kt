package com.bnorm.react

import com.bnorm.react.components.ItemList
import com.bnorm.react.components.TodoDate
import react.RBuilder

@RFunction
fun RBuilder.App() {
  AppStateProvider {
    TodoDate()
    ItemList()
  }
}
