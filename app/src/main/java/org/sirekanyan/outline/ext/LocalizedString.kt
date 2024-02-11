package org.sirekanyan.outline.ext

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

fun Context.getString(resource: LocalizedString): String =
    when (resource) {
        is StringResource ->
            resources.getString(resource.id)
        is PluralsResource ->
            resources.getQuantityString(resource.id, resource.quantity, resource.quantity)
    }

sealed class LocalizedString

class StringResource(@StringRes val id: Int) : LocalizedString()

class PluralsResource(@PluralsRes val id: Int, val quantity: Int) : LocalizedString()
