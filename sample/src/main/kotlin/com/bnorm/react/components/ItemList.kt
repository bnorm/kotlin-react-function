package com.bnorm.react.components

import com.bnorm.react.*
import com.bnorm.react.styles.div
import kotlinx.html.js.onClickFunction
import reach.accordion.*
import react.Fragment
import react.RBuilder
import react.dom.*
import com.bnorm.react.components.ItemListStyles as styles

@RFunction
fun RBuilder.ItemList() {
  val dispatch = useAppReducer()
  val (pending, paused, completed) = useItems()

  div(classes = "item-list") {
    Progress()
    AddItemForm()
    if (pending.isNotEmpty()) {
      Fragment {
        pending.forEach {
          Item(it)
        }
      }
    } else {
      div(className = styles.alldone) {
        img(src = "img/alldone.svg", alt = "Nothing to do!") {}
      }
    }

    if (completed.isNotEmpty() || paused.isNotEmpty()) {
      Accordion(collapsible = true, multiple = true) {
        if (paused.isNotEmpty()) {
          AccordionItem {
            AccordionButton(className = styles.toggle) {
              img(src = "img/arrow.svg", alt = "Do Later Toggle") {}
              span { +"Do Later" }
            }
            AccordionPanel(className = styles.panel) {
              for (item in paused) {
                Item(item)
              }
            }
          }
        }
        if (completed.isNotEmpty()) {
          AccordionItem {
            AccordionButton(className = styles.toggle) {
              img(src = "img/arrow.svg", alt = "Completed Toggle") {}
              span { +"Completed" }
            }
            AccordionPanel(className = styles.panel) {
              for (item in completed) {
                Item(item)
              }
            }
          }
        }
      }

      div(className = styles.reset) {
        button {
          attrs.onClickFunction = { dispatch(AppAction.ResetAll) }
          +"reset progress"
        }
      }
    }
  }
}
