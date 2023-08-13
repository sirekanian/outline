package org.sirekanyan.outline.text

fun formatTraffic(bytes: Long): String =
    when {
        bytes > 1_000_000_000 -> "${bytes / 1000 / 1000 / 1000} GB"
        bytes > 1_000_000 -> "${bytes / 1000 / 1000} MB"
        bytes > 1_000 -> "${bytes / 1000} kB"
        else -> "$bytes B"
    }
