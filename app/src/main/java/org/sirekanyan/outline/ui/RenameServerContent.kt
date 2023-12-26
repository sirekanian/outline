package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.app
import org.sirekanyan.outline.repository.ServerRepository

@Composable
private fun rememberRenameServerDelegate(router: Router, server: Server): RenameDelegate {
    val context = LocalContext.current
    val servers = remember { context.app().serverRepository }
    return remember(server) { RenameServerDelegate(router, servers, server) }
}

private class RenameServerDelegate(
    private val router: Router,
    private val servers: ServerRepository,
    private val server: Server,
) : RenameDelegate {
    override suspend fun onRename(newName: String) {
        val newServer = servers.renameServer(server, newName)
        router.page = SelectedPage(newServer)
    }
}

@Composable
fun RenameServerContent(router: Router, server: Server) {
    val delegate = rememberRenameServerDelegate(router, server)
    val state = rememberRenameState(router, delegate)
    RenameContent(state, router, "Edit server", server.name, server.getHost())
}
