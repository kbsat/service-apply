package apply.ui.admin

import apply.application.RecruitmentResponse
import apply.ui.admin.mission.MissionsView
import apply.ui.admin.selections.SelectionView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.RouterLink

infix fun String.of(navigationTarget: Class<out Component>): MenuItem {
    return SingleMenuItem(this, navigationTarget)
}

infix fun String.comboBoxOf(recruitments: List<RecruitmentResponse>): MenuItem {
    return ComboBoxMenuItem(this, recruitments.reversed())
}

sealed class MenuItem(protected val title: String) {
    abstract fun toComponents(): List<Component>
}

class SingleMenuItem(
    title: String,
    private val navigationTarget: Class<out Component>
) : MenuItem(title) {
    override fun toComponents(): List<Component> {
        return listOf(
            Tab(
                RouterLink(title, navigationTarget).apply {
                    style.set("justify-content", "center")
                }
            )
        )
    }
}

class ComboBoxMenuItem(
    placeholder: String,
    private val contents: List<RecruitmentResponse>
) : MenuItem(placeholder) {
    override fun toComponents(): List<Component> {

        val missionsLink = "과제 관리" routeOf MissionsView::class.java
        val selectionLink = "선발 과정" routeOf SelectionView::class.java
        selectionLink.style.set("font-weight", "900")

        val missionsTab = createHiddenTab(missionsLink)
        val selectionTab = createHiddenTab(selectionLink)

        val comboBox = ComboBox<RecruitmentResponse>("모집을 선택해 선발을 진행하세요.").apply {
            placeholder = title
            setItems(contents)
            setItemLabelGenerator { it.title }

            addValueChangeListener {
                routeEvent(missionsTab, missionsLink, MissionsView::class.java)
                routeEvent(selectionTab, selectionLink, SelectionView::class.java)
            }
        }

        return listOf(
            createLineTab(),
            Tab(comboBox),
            missionsTab,
            selectionTab,
            createLineTab()
        )
    }

    private fun ComboBox<RecruitmentResponse>.routeEvent(
        tab: Tab,
        link: RouterLink,
        navigationTarget: Class<out HasUrlParamLayout<Long>>
    ) {
        tab.isVisible = true
        tab.isSelected = false
        link.setRoute(navigationTarget, value.id)
    }

    private infix fun String.routeOf(navigationTarget: Class<out HasUrlParamLayout<Long>>): RouterLink {
        return RouterLink(this, navigationTarget, 0).apply {
            style.set("justify-content", "center")
        }
    }

    private fun createLineTab(): Tab = Tab(
        Div().apply {
            setWidthFull()
            style.set("border-top", "1px solid gray")
        }
    )

    private fun createHiddenTab(link: RouterLink): Tab = Tab(link).apply {
        isVisible = false
    }
}
