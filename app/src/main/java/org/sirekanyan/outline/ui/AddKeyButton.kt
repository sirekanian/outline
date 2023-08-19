package org.sirekanyan.outline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddKeyButton(isVisible: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    Box(Modifier.fillMaxSize().navigationBarsPadding(), Alignment.BottomEnd) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            FloatingActionButton(onClick, Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.animateContentSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Add, null)
                        Spacer(Modifier.width(12.dp))
                        Text("Add key")
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}
