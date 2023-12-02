package org.sirekanyan.outline.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
