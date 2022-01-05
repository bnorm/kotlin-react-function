package com.bnorm.react

import kotlinx.datetime.*
import react.*
import react.dom.div

private val AppContext = createContext<ReducerInstance<AppState, AppAction>>()

fun useAppState(): AppState = useContext(AppContext).component1()

fun useAppReducer(): Dispatch<AppAction> = useContext(AppContext).component2()

data class Items(val pending: List<TodoItem>, val paused: List<TodoItem>, val completed: List<TodoItem>)

fun useItems(): Items {
  val (items) = useAppState()
  return Items(
    pending = items.filter { it.status == ItemStatus.Pending },
    paused = items.filter { it.status == ItemStatus.Paused },
    completed = items.filter { it.status == ItemStatus.Completed },
  )
}

sealed class AppAction {
  class AddItem(
    val item: TodoItem
  ) : AppAction()

  class UpdateItem(
    val item: TodoItem
  ) : AppAction()

  class DeleteItem(
    val item: TodoItem
  ) : AppAction()

  object ResetAll : AppAction()
}

val appStateReducer: Reducer<AppState, AppAction> = { state, action ->
  when (action) {
    is AppAction.AddItem -> {
      val newItems = state.items + action.item
      state.copy(items = newItems)
    }
    is AppAction.UpdateItem -> {
      val newItems = state.items.map { item ->
        if (item.key == action.item.key) action.item
        else item
      }
      state.copy(items = newItems)
    }
    is AppAction.DeleteItem -> {
      val newItems = state.items.filter { it.key != action.item.key }
      state.copy(items = newItems)
    }
    is AppAction.ResetAll -> {
      val newItems = state.items
        .filter { it.status != ItemStatus.Completed }
        .map { it.copy(status = ItemStatus.Pending) }
      state.copy(items = newItems, date = Clock.System.todayAt(TimeZone.currentSystemDefault()))
    }
  }.also { saveState(it) }
}

@RFunction
fun RBuilder.AppStateProvider(block: RBuilder.() -> Unit) {
  val initialState = loadState()
  saveState(initialState)

  val value = useReducer(appStateReducer, initialState)
  div(classes = "App") {
    AppContext.Provider(value) {
      block()
    }
  }
}
