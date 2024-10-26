package com.app.routineturboa.shared

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

sealed class ActiveOverlayComponent {
    data object FullEditing : ActiveOverlayComponent()
    data object AddingNew : ActiveOverlayComponent()

    // All these must not disable main TasksLazyColumn
    data object QuickEditing : ActiveOverlayComponent()
    data object ShowingDetails : ActiveOverlayComponent()
    data object ShowingCompletedTasks : ActiveOverlayComponent()
    data object ShowingDatePicker : ActiveOverlayComponent()

    data object None: ActiveOverlayComponent()
}
