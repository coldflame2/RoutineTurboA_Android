package com.app.routineturboa.shared.states

/**
 *     data object ShowingTasksList : ActiveUiComponent()
 *
 *     // All these must not disable main TasksLazyColumn
 *     data object QuickEditing : ActiveUiComponent()
 *     data object ShowingDetails : ActiveUiComponent()
 *     data object ShowingCompletedTasks : ActiveUiComponent()
 *     data object ShowingDatePicker : ActiveUiComponent()
 *
 *     data object FullEditing : ActiveUiComponent()
 *     data object AddingNew : ActiveUiComponent()
 *
 *     data object None: ActiveUiComponent()
 */

sealed class ActiveUiComponent {
    open fun shouldShowLazyColumn(): Boolean? = true

    data object FullEditing : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }

    data object AddingNew : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = false
    }

    data object QuickEditOverlay : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }

    data object DetailsView : ActiveUiComponent() {
        override fun shouldShowLazyColumn() = true
    }

    data object FinishedTasks : ActiveUiComponent()

    data object DatePicker : ActiveUiComponent()

    data object None : ActiveUiComponent()
}
