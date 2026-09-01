package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun ConfigScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Offline DRM Rules Card
        ConfigCard(
            title = "Offline DRM Rules",
            subtitle = "These settings apply to all users unless overridden in their profile.",
            icon = Icons.Default.Security,
            iconTint = PrimaryBlue
        ) {
            NumberSettingRow(icon = Icons.Default.Event, title = "Default Offline Days", subtitle = "Number of days content can be accessed offline.", value = 2)
            Spacer(modifier = Modifier.height(16.dp))
            NumberSettingRow(icon = Icons.Default.Campaign, title = "Default Forced Ads", subtitle = "Number of ads to show during offline playback.", value = 5)
            Spacer(modifier = Modifier.height(24.dp))
            GradientButton("Update DRM Config", Icons.Default.Security)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic OTA Updates Card
        ConfigCard(
            title = "Dynamic OTA Updates",
            subtitle = "Update scrapers, providers, or endpoints globally for the main CineStream app in real-time.",
            icon = Icons.Default.CloudUpload,
            iconTint = PrimaryPurple
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Providers JSON", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Box(
                    modifier = Modifier
                        .background(PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Format", color = PrimaryPurple, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Code Editor Mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(12.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row {
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(24.dp)) {
                        (1..5).forEach {
                            Text(it.toString(), color = TextSecondary.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("{", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        Text("  \"providers\": [],", color = SuccessGreen, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        Text("  \"scrapers\": [],", color = SuccessGreen, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        Text("  \"endpoints\": []", color = SuccessGreen, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        Text("}", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            GradientButton("Deploy OTA Update", Icons.Default.RocketLaunch, brush = Brush.horizontalGradient(listOf(PrimaryPurple, PrimaryBlue)))
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Quick Actions", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(icon = Icons.Default.Storage, title = "Clear Cache", subtitle = "Free up storage", iconTint = PrimaryBlue, modifier = Modifier.weight(1f))
            QuickActionCard(icon = Icons.Default.Sync, title = "Sync Now", subtitle = "Sync all data", iconTint = SuccessGreen, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(icon = Icons.Default.VerifiedUser, title = "Backup Config", subtitle = "Save current config", iconTint = WarningOrange, modifier = Modifier.weight(1f))
            QuickActionCard(icon = Icons.Default.Restore, title = "Restore Config", subtitle = "Restore from backup", iconTint = ErrorRed, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ConfigCard(title: String, subtitle: String, icon: ImageVector, iconTint: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}

@Composable
fun NumberSettingRow(icon: ImageVector, title: String, subtitle: String, value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground, RoundedCornerShape(12.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
        Row(
            modifier = Modifier
                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                .border(1.dp, DividerColor, RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Text("-", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            Text(value.toString(), color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Text("+", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GradientButton(text: String, icon: ImageVector, brush: Brush = Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.7f)))) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(brush, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun QuickActionCard(icon: ImageVector, title: String, subtitle: String, iconTint: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(16.dp))
            .clickable { }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DarkBackground, RoundedCornerShape(12.dp))
                .border(1.dp, DividerColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
    }
}
