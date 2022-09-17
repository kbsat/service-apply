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

fun String.comboBoxOf(recruitments: List<RecruitmentResponse>): MenuItem {
    return ComboBoxMenuItem(this, recruitments.map { it.toContent() })
}

private fun RecruitmentResponse.toContent(): ComboBoxContent = ComboBoxContent(id, title)

sealed class MenuItem(protected val title: String) {
    abstract fun toComponent(): Component
}

class SingleMenuItem(
    title: String,
    private val navigationTarget: Class<out Component>
) : MenuItem(title) {
    override fun toComponent(): Component {
        return Tab(RouterLink(title, navigationTarget).apply {
            style.set("justify-content", "center")
        })
    }
}

data class ComboBoxContent(val id: Long, val title: String)

class ComboBoxMenuItem(
    placeholder: String,
    private val contents: List<ComboBoxContent>
) : MenuItem(placeholder) {
    override fun toComponent(): Component {
        val associateBy = contents.associateBy { it.title }

        val layout = VerticalLayout().apply {
            element.style.set("width", "85%")
        }

        val missionsLink = RouterLink("과제 관리", MissionsView::class.java, 0)
        val selectionLink = RouterLink("선발 과정", SelectionView::class.java, 0)

        val hiddenTabs = createTabs(listOf(Tab(missionsLink), Tab(selectionLink)))
            .apply {
                element.style.set("margin", "0 auto")
                isVisible = false
            }

        val comboBox = ComboBox<String>().apply {
            placeholder = title
            setItems(contents.map { it.title })

            addValueChangeListener {
                val id = associateBy[it.value]!!.id
                hiddenTabs.isVisible = true
                missionsLink.setRoute(MissionsView::class.java, id)
                selectionLink.setRoute(SelectionView::class.java, id)
            }
        }
        layout.apply {
            add(comboBox)
            add(hiddenTabs)
        }
        return layout
    }
}
