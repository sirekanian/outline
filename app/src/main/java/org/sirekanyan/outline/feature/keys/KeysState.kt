package org.sirekanyan.outline.feature.keys

sealed class KeysState

data object KeysIdleState : KeysState()

data object KeysLoadingState : KeysState()

data object KeysErrorState : KeysState()
