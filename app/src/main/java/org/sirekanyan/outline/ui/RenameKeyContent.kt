package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.app
import org.sirekanyan.outline.repository.KeyRepository

@Composable
private fun rememberRenameKeyDelegate(key: Key): RenameDelegate {
    val context = LocalContext.current
    val keys = remember { context.app().keyRepository }
    return remember(key) { RenameKeyDelegate(keys, key) }
}

private class RenameKeyDelegate(
    private val keys: KeyRepository,
    private val key: Key,
) : RenameDelegate {
    override suspend fun onRename(newName: String) {
        keys.renameKey(key.server, key, newName)
        try {
            keys.updateKeys(key.server)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}

@Composable
fun RenameKeyContent(router: Router, key: Key) {
    val delegate = rememberRenameKeyDelegate(key)
    val state = rememberRenameState(router, delegate)
    RenameContent(state, router, "Edit key", key.name, key.defaultName)
}
