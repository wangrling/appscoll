package com.android.home.todomvp.tasks;

/**
 * Used with the filter in the tasks list.
 */


public enum TasksFilterType {

    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only a active (not completed yet)
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS
}
