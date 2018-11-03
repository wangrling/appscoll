package com.android.home.plaid.core.data

/**
 * Base class for all model types.
 */

abstract class PlaidItem(
    @Transient open val id: Long,
    @Transient open val title: String,
    @Transient open val url: String? = null
) {
    var dataSource: String ?= null
    var page: Int = 0
    var weight: Float = 0F  // Used for sorting
    var colspan: Int = 0
}

