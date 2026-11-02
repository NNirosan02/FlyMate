package com.nnapps.flymate.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nnapps.flymate.ui.theme.DarkBackground
import com.nnapps.flymate.ui.theme.NeonCyan

@Composable
fun BottomActionBar(
    modifier: Modifier = Modifier,
    onNotifyClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    // Gradient overlay to seamlessly fade into the dark background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        DarkBackground.copy(alpha = 0.95f),
                        DarkBackground
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .navigationBarsPadding() // Ensures safety across gesture bars / Android 3-button nav
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notify Me Pill
            Button(
                onClick = onNotifyClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1B2B)),
                border = BorderStroke(1.dp, Color(0xFF1E2E42)),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                modifier = Modifier.weight(1.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Notify Me",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Save Flight Pill
            Button(
                onClick = onSaveClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1B2B)),
                border = BorderStroke(1.dp, Color(0xFF1E2E42)),
                shape = RoundedCornerShape(50.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                modifier = Modifier.weight(1.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Save Flight",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Share Button
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF0F1B2B), RoundedCornerShape(50.dp))
                    .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(50.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Overflow/More Button
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF0F1B2B), RoundedCornerShape(50.dp))
                    .border(1.dp, Color(0xFF1E2E42), RoundedCornerShape(50.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}