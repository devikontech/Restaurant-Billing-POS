package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TitleWithIcon(
    modifier : Modifier = Modifier,
    textModifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector,
    showScrollToTop: Boolean = false,
    onClickScrollToTop: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            modifier = textModifier,
            text = text,
            icon = icon,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge
        )

        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            IconButton(
                onClick = onClickScrollToTop,
                modifier = iconModifier
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowCircleUp,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}