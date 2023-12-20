package org.sirekanyan.outline.feature.sort

import androidx.annotation.StringRes
import org.sirekanyan.outline.R
import org.sirekanyan.outline.api.model.Key

enum class Sorting(val key: String, @StringRes val title: Int, val comparator: Comparator<Key>) {

    ID(
        key = "id",
        title = R.string.outln_sorting_by_id,
        comparator = compareBy { it.id.toLongOrNull() },
    ),

    NAME(
        key = "name",
        title = R.string.outln_sorting_by_name,
        comparator = compareBy<Key> { it.name.isEmpty() }
            .thenBy { it.name.lowercase() }
            .thenBy { it.id.toLongOrNull() },
    ),

    TRAFFIC(
        key = "traffic",
        title = R.string.outln_sorting_by_traffic,
        comparator = compareByDescending<Key> { it.traffic }
            .thenBy { it.id.toLongOrNull() },
    );

    companion object {

        init {
            check(entries.distinctBy(Sorting::key).size == entries.size) { "Keys must be unique" }
        }

        const val KEY = "Sorting"
        val DEFAULT = TRAFFIC

        fun getByKey(key: String?): Sorting =
            key?.let(::findByKey) ?: DEFAULT

        private fun findByKey(key: String): Sorting? =
            entries.find { it.key == key }

    }

}
