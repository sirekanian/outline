package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import org.sirekanyan.outline.MainState
import org.sirekanyan.outline.RenameServerDialog
import org.sirekanyan.outline.SelectedPage

@Composable
fun RenameServerContent(state: MainState, dialog: RenameServerDialog) {
    RenameContent(state, "Edit server", dialog.server.name, dialog.server.getHost()) { newName ->
        val newServer = state.servers.renameServer(dialog.server, newName)
        state.page = SelectedPage(newServer)
    }
}
