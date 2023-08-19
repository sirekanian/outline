package org.sirekanyan.outline.feature.keys

import org.sirekanyan.outline.api.model.Key

sealed class KeysState

data object KeysLoadingState : KeysState()

data object KeysErrorState : KeysState()

data class KeysSuccessState(val values: List<Key>) : KeysState()
