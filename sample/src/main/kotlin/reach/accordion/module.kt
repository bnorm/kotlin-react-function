@file:JsModule("@reach/accordion")
@file:JsNonModule

package reach.accordion

import react.ComponentClass
import react.Props

external val Accordion: ComponentClass<AccordionProps>

external interface AccordionProps : Props {
  var collapsible: Boolean
  var multiple: Boolean
}

external val AccordionItem: ComponentClass<AccordionItemProps>

external interface AccordionItemProps : Props

external val AccordionButton: ComponentClass<AccordionButtonProps>

external interface AccordionButtonProps : Props

external val AccordionPanel: ComponentClass<AccordionPanelProps>

external interface AccordionPanelProps : Props
