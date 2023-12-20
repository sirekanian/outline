package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.RenameServerDialog

@Composable
fun RenameServerContent(state: MainState, dialog: RenameServerDialog) {
    RenameContent(state, "Edit server", dialog.server.name, dialog.server.getHost()) { newName ->
        state.servers.renameServer(dialog.server, newName)
        state.refreshCurrentKeys(showLoading = false)
    }
}
