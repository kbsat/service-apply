package apply.ui.admin

import apply.application.RecruitmentResponse
import apply.ui.admin.mission.MissionsView
import apply.ui.admin.selections.SelectionView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.RouterLink
import support.views.createTabs

infix fun String.of(navigationTarget: Class<out Component>): MenuItem {
    return SingleMenuItem(this, navigationTarget)
}

infix fun String.comboBoxOf(recruitments: List<RecruitmentResponse>): MenuItem {
    return ComboBoxMenuItem(this, recruitments)
}

sealed class MenuItem(protected val title: String) {
    abstract fun toComponent(): Component
}

class SingleMenuItem(
    title: String,
    private val navigationTarget: Class<out Component>
) : MenuItem(title) {
    override fun toComponent(): Component {
        return Tab(
            RouterLink(title, navigationTarget).apply {
                style.set("justify-content", "center")
            }
        )
    }
}

class ComboBoxMenuItem(
    placeholder: String,
    private val contents: List<RecruitmentResponse>
) : MenuItem(placeholder) {
    override fun toComponent(): Component {
        val layout = VerticalLayout().apply {
            element.style.set("width", "85%")
        }

        val missionsLink = RouterLink("과제 관리", MissionsView::class.java, 0)
            .apply {
                addAttachListener {
                    println(this.parent.get().id)
                }
            }
        val selectionLink = RouterLink("선발 과정", SelectionView::class.java, 0)

        val hiddenTabs = createTabs(listOf(Tab(missionsLink), Tab(selectionLink)))
            .apply {
                element.style.set("margin", "0 auto")
                isVisible = false
            }

        val comboBox = ComboBox<RecruitmentResponse>("선발과정으로 평가를 시작하세요.").apply {
            placeholder = title
            setItems(contents)
            setItemLabelGenerator { it.title }

            addValueChangeListener {
                hiddenTabs.isVisible = true
                missionsLink.setRoute(MissionsView::class.java, value.id)
                selectionLink.setRoute(SelectionView::class.java, value.id)
            }
        }
        layout.apply {
            add(comboBox)
            add(hiddenTabs)
        }
        return layout
    }
}
