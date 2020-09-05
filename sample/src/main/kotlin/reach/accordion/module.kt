@file:JsModule("@reach/accordion")
@file:JsNonModule

package reach.accordion

import react.RClass
import react.dom.WithClassName

external val Accordion: RClass<AccordionProps>

external interface AccordionProps : WithClassName {
  var collapsible: Boolean
  var multiple: Boolean
}

external val AccordionItem: RClass<AccordionItemProps>

external interface AccordionItemProps : WithClassName

external val AccordionButton: RClass<AccordionButtonProps>

external interface AccordionButtonProps : WithClassName

external val AccordionPanel: RClass<AccordionPanelProps>

external interface AccordionPanelProps : WithClassName
