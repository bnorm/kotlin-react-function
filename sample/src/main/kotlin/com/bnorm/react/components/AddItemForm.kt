package com.bnorm.react.components

import com.bnorm.react.*
import com.bnorm.react.styles.form
import kotlinx.html.ButtonType
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.dom.button
import react.dom.input
import react.useRef
import com.bnorm.react.components.AddItemFormStyles as styles

@RFunction
fun RBuilder.AddItemForm() {
  val dispatch = useAppReducer()
  val inputRef = useRef<HTMLInputElement?>(null)

  fun addItem(e: Event) {
    val inputElement = inputRef.current!!

    val text = inputElement.value.trim()
    if (text.isNotEmpty()) {
      dispatch(AppAction.AddItem(TodoItem(text = text)))
    }

    e.preventDefault()
    inputElement.value = ""
    inputElement.focus()
  }

  form(styles.form) {
    attrs.onSubmitFunction = { addItem(it) }
    input {
      ref = inputRef
      attrs.placeholder = "Add new item"
      attrs.autoFocus = true
    }
    button(type = ButtonType.submit) {}
  }
}
