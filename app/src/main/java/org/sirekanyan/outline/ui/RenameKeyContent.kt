package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.RenameKeyDialog

@Composable
fun RenameKeyContent(state: MainState, dialog: RenameKeyDialog) {
    val accessKey = dialog.key.accessKey
    RenameContent(state, "Edit key", accessKey.name, accessKey.defaultName) { newName ->
        state.keys.renameKey(dialog.server, accessKey, newName)
        state.refreshCurrentKeys(showLoading = false)
        state.refreshHelloPage(dialog.server)
    }
}
