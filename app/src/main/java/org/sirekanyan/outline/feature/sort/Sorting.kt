package org.sirekanyan.outline.feature.sort

import androidx.annotation.StringRes
import org.sirekanyan.outline.R
import org.sirekanyan.outline.api.model.Key

enum class Sorting(val key: String, @StringRes val title: Int, val comparator: Comparator<Key>) {

    ID(
        key = "id",
        title = R.string.outln_sorting_by_id,
        comparator = compareBy { it.accessKey.id.toLongOrNull() },
    ),

    NAME(
        key = "name",
        title = R.string.outln_sorting_by_name,
        comparator = compareBy({ it.accessKey.name.isEmpty() }, { it.accessKey.name.lowercase() }),
    ),

    TRAFFIC(
        key = "traffic",
        title = R.string.outln_sorting_by_traffic,
        comparator = compareByDescending { it.traffic },
    );

    companion object {

        init {
            check(values().distinctBy(Sorting::key).size == values().size) { "Keys must be unique" }
        }

        const val KEY = "Sorting"
        val DEFAULT = TRAFFIC

        fun getByKey(key: String?): Sorting =
            key?.let(::findByKey) ?: DEFAULT

        private fun findByKey(key: String): Sorting? =
            values().find { it.key == key }

    }

}
