@file:Suppress("FunctionName")

package com.bnorm.react

import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.button
import react.dom.div
import react.dom.h3
import react.dom.input
import react.dom.li
import react.dom.render
import react.dom.ul
import react.getValue
import react.setValue
import react.useState

fun main() {
  document.getElementById("root")?.let {
    render(it) {
      TodoList(listOf(TodoItem("Hello"), TodoItem("World")))
    }
  }
}

data class TodoItem(
  val text: String
)

@RFunction
fun RBuilder.TodoList(
  initialItems: List<TodoItem> = listOf()
) {
  var items by useState(initialItems)
  var text by useState("")

  div {
    input(type = InputType.text, name = "itemText") {
      attrs {
        value = text
        placeholder = "Add a to-do item"
        onChangeFunction = { text = it.target.unsafeCast<HTMLInputElement>().value }
      }
    }

    button {
      +"Add"
      attrs.onClickFunction = {
        if (text.isNotEmpty()) {
          items += TodoItem(text.trim())
          text = ""
        }
      }
    }

    h3 {
      ul {
        for (item in items) {
          li { TodoListItem(item, onRemove = { items -= item }) }
        }
      }
    }
  }
}

@RFunction
fun RBuilder.TodoListItem(
  @RKey item: TodoItem,
  onRemove: () -> Unit = {}
) {
  +item.text
  button {
    +"×"
    attrs.onClickFunction = { onRemove() }
  }
}
