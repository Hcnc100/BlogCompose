package com.nullpointer.blogcompose.models.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TypeSnackBar(
    val color: Color,
    val icon: ImageVector
) {
    INFO(
        color = Color(0xFF3F51B5),
        icon = Icons.Filled.Info
    ),
    SUCCESS(
        color = Color(0xFF4CAF50),
        icon = Icons.Filled.Check
    ),
    ERROR(
        color = Color(0xFFF44336),
        icon = Icons.Filled.Close
    ),
    WARNING(
        color = Color(0xFFFF9800),
        icon = Icons.Filled.Warning
    ),
    RETRY(
        color = Color(0xFF607D8B),
        icon = Icons.Filled.Refresh
    )
}