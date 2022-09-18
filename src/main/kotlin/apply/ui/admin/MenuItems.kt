package apply.ui.admin

import apply.application.RecruitmentResponse
import apply.ui.admin.mission.MissionsView
import apply.ui.admin.selections.SelectionView
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.combobox.ComboBox
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
        val missionsLink = RouterLink("과제 관리", MissionsView::class.java, 0).apply {
            style.set("justify-content", "center")
        }
        val selectionLink = RouterLink("선발 과정", SelectionView::class.java, 0).apply {
            style.set("justify-content", "center")
            style.set("font-weight", "900")
        }

        val missionsTab = createHiddenTab(missionsLink)
        val selectionTab = createHiddenTab(selectionLink)

        val comboBox = ComboBox<RecruitmentResponse>("모집을 고르고 선발과정에서 평가하세요.").apply {

            placeholder = title
            setItems(contents)
            setItemLabelGenerator { it.title }

            addValueChangeListener {
                missionsTab.isVisible = true
                missionsTab.isSelected = false
                missionsLink.setRoute(MissionsView::class.java, value.id)

                selectionTab.isVisible = true
                selectionTab.isSelected = false
                selectionLink.setRoute(SelectionView::class.java, value.id)
            }
        }
        // val horizontalLayout = HorizontalLayout().apply {
        //     alignItems = FlexComponent.Alignment.CENTER
        // }
        //
        // val questionCircle = VaadinIcon.QUESTION_CIRCLE_O.create()
        // val tooltip = Tooltip()
        // tooltip.attachToComponent(questionCircle)
        // tooltip.add(Paragraph("모집을 선택하고 ‘선발 과정’에서 지원자를 평가하세요!"))
        //
        // horizontalLayout.add(
        //     comboBox,
        //     questionCircle,
        //     tooltip
        // )

        return listOf(Tab(comboBox), missionsTab, selectionTab)
    }

    private fun createHiddenTab(link: RouterLink): Tab = Tab(link).apply {
        isVisible = false
    }
}
