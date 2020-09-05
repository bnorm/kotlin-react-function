package reach.accordion

import kotlinx.css.RuleSet
import react.RBuilder
import styled.css
import styled.styled

fun RBuilder.Accordion(
  className: RuleSet? = null,
  collapsible: Boolean,
  multiple: Boolean,
  block: RBuilder.() -> Unit,
) {
  styled(Accordion).invoke(this) {
    className?.let { css(it) }
    attrs {
      this.collapsible = collapsible
      this.multiple = multiple
    }
    block()
  }
}

fun RBuilder.AccordionItem(
  className: RuleSet? = null,
  block: RBuilder.() -> Unit,
) {
  styled(AccordionItem).invoke(this) {
    className?.let { css(it) }
    block()
  }
}

fun RBuilder.AccordionButton(
  className: RuleSet? = null,
  block: RBuilder.() -> Unit,
) {
  styled(AccordionButton).invoke(this) {
    className?.let { css(it) }
    block()
  }
}

fun RBuilder.AccordionPanel(
  className: RuleSet? = null,
  block: RBuilder.() -> Unit,
) {
  styled(AccordionPanel).invoke(this) {
    className?.let { css(it) }
    block()
  }
}
