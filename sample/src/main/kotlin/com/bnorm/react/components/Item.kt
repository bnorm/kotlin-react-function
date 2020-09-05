package com.bnorm.react.components

import com.bnorm.react.*
import com.bnorm.react.styles.button
import com.bnorm.react.styles.div
import kotlinx.html.tabIndex
import react.RBuilder
import styled.*
import com.bnorm.react.components.ItemStyles as styles

@RFunction
fun RBuilder.Item(item: TodoItem, @RKey key: String = item.key) {
  val dispatch = useAppReducer()
  val text = item.text
  val paused = item.status == ItemStatus.Paused
  val completed = item.status == ItemStatus.Completed

  fun deleteItem() {
    dispatch(AppAction.DeleteItem(item))
  }

  fun pauseItem() {
    val pausedItem = item.copy(status = ItemStatus.Paused)
    dispatch(AppAction.UpdateItem(pausedItem))
  }

  fun resumeItem() {
    val pendingItem = item.copy(status = ItemStatus.Pending)
    dispatch(AppAction.UpdateItem(pendingItem))
  }

  fun completeItem() {
    val completedItem = item.copy(status = ItemStatus.Completed)
    dispatch(AppAction.UpdateItem(completedItem))
  }

  div(className = styles.item) {
    attrs.tabIndex = "0"

    div(className = styles.itemName) { +text }

    styledDiv {
      css {
        +styles.buttons
        if (completed) +styles.completedButtons
      }

      button(className = styles.delete, onClick = { deleteItem() }, tabIndex = "0")
      if (!paused && !completed) {
        button(className = styles.pause, onClick = { pauseItem() }, tabIndex = "0")
      }
      if (paused && !completed) {
        button(className = styles.resume, onClick = { resumeItem() }, tabIndex = "0")
      }
      if (!completed) {
        button(className = styles.complete, onClick = { completeItem() }, tabIndex = "0")
      }
    }
  }
}
