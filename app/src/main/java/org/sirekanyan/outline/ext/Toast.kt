package org.sirekanyan.outline.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(text: LocalizedString) {
    Toast.makeText(this, getString(text), Toast.LENGTH_SHORT).show()
}
