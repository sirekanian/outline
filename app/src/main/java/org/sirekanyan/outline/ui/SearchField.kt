package org.sirekanyan.outline.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import org.sirekanyan.outline.R

@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val contentColor = LocalContentColor.current
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        textStyle = MaterialTheme.typography.titleLarge.copy(color = contentColor),
        cursorBrush = SolidColor(contentColor),
        singleLine = true,
    )
    if (query.isEmpty()) {
        Text(stringResource(R.string.outln_hint_search), color = contentColor.copy(alpha = 0.38f))
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
