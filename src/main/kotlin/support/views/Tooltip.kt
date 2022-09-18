package support.views

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import java.util.Objects

@Tag("vcf-tooltip")
@NpmPackage(value = "@vaadin-component-factory/vcf-tooltip", version = "1.3.15")
@JsModule("@vaadin-component-factory/vcf-tooltip/src/vcf-tooltip.js")
class Tooltip : Component(), HasComponents {

    init {
        element.style["margin"] = "0px"
        element.setProperty("position", "bottom")
        element.setProperty("align", "left")
    }

    fun attachToComponent(component: Component) {
        Objects.requireNonNull(component)
        element.node.runWhenAttached { ui: UI ->
            ui.page.executeJs(
                "$0.targetElement = $1;",
                element, component.element
            )
        }
    }
}
