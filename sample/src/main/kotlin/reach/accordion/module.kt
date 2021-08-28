@file:JsModule("@reach/accordion")
@file:JsNonModule

package reach.accordion

import react.ComponentClass
import react.Props
import react.RBuilder
import react.dom.WithClassName
import react.fc

external val Accordion: ComponentClass<AccordionProps>

external interface AccordionProps : WithClassName {
  var collapsible: Boolean
  var multiple: Boolean
}

external val AccordionItem: ComponentClass<AccordionItemProps>

external interface AccordionItemProps : WithClassName

external val AccordionButton: ComponentClass<AccordionButtonProps>

external interface AccordionButtonProps : WithClassName

external val AccordionPanel: ComponentClass<AccordionPanelProps>

external interface AccordionPanelProps : WithClassName
