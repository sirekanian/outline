package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.RenameKeyDialog

@Composable
fun RenameKeyContent(state: MainState, dialog: RenameKeyDialog) {
    RenameContent(state, "Edit key", dialog.key.name, dialog.key.defaultName) { newName ->
        state.keys.renameKey(dialog.key.server, dialog.key, newName)
        state.updateServerKeys(dialog.key.server)
    }
}
