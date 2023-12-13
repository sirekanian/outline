package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.RenameServerDialog
import org.sirekanyan.outline.api.model.getHost

@Composable
fun RenameServerContent(state: MainState, dialog: RenameServerDialog) {
    RenameContent(state, "Edit server", dialog.server.name, dialog.server.getHost()) { newName ->
        state.servers.renameServer(dialog.server, newName)
    }
}
