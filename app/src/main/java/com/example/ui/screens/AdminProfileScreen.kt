package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminProfileScreen(
    onLogoutClick: () -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // Profile Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(24.dp))
                .border(1.dp, DividerColor, RoundedCornerShape(24.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.linearGradient(colors = listOf(PrimaryBlue.copy(alpha=0.5f), PrimaryBlue.copy(alpha=0.1f))),
                            shape = CircleShape
                        )
                        .border(1.dp, PrimaryBlue.copy(alpha=0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .background(SuccessGreen, CircleShape)
                            .border(3.dp, DarkSurface, CircleShape)
                            .offset(x = (-8).dp, y = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("System Administrator", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .background(PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Administrator", color = PrimaryBlue, style = MaterialTheme.typography.labelMedium)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Full access to all system features", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Account Information
        SectionCard(title = "Account Information", icon = Icons.Default.Person) {
            InfoRow(icon = Icons.Default.Email, title = "Email Address", value = currentUser?.email ?: "Unknown", canCopy = true)
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
            InfoRow(icon = Icons.Default.Badge, title = "Admin UID", value = currentUser?.uid ?: "N/A", canCopy = true)
        }

        // Account Security
        SectionCard(title = "Account Security", icon = Icons.Default.Security) {
            SecurityRow(icon = Icons.Default.Lock, title = "Password", subtitle = "Last changed 30 days ago", iconTint = SuccessGreen)
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
            SecurityRow(icon = Icons.Default.Computer, title = "Active Sessions", subtitle = "1 active session", iconTint = PrimaryPurple)
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(start = 56.dp))
            SecurityRow(icon = Icons.Default.VpnKey, title = "Two-Factor Authentication", subtitle = "Disabled", iconTint = WarningOrange)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorRed.copy(alpha = 0.1f),
                contentColor = ErrorRed
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.3f))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Logout", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Sign out from this account", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(16.dp))
            .border(1.dp, DividerColor, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider(color = DividerColor)
        content()
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, value: String, canCopy: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(DarkBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            Text(value, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
        if (canCopy) {
            IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(32.dp).background(DarkBackground, RoundedCornerShape(8.dp))) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SecurityRow(icon: ImageVector, title: String, subtitle: String, iconTint: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(DarkBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
    }
}
