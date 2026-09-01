package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodels.UserDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    onBackClick: () -> Unit,
) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserDetailViewModel(userId) as T
        }
    }
    val viewModel: UserDetailViewModel = viewModel(factory = factory)
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val u = user!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header info
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(u.username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(u.email, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ID: ${u.id}", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Subscription
                Text("Subscription", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    headlineContent = { Text("Premium Plan (PRO)") },
                    supportingContent = { Text("Grant user premium access") },
                    trailingContent = {
                        Switch(checked = u.isPremium, onCheckedChange = { viewModel.togglePremium() })
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Social Bans
                Text("Social Constraints", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    headlineContent = { Text("Global Chat Ban") },
                    trailingContent = {
                        Switch(checked = u.chatBan, onCheckedChange = { viewModel.toggleBan("chatBan", u.chatBan) })
                    }
                )
                ListItem(
                    headlineContent = { Text("Story Creation Ban") },
                    trailingContent = {
                        Switch(checked = u.storyBan, onCheckedChange = { viewModel.toggleBan("storyBan", u.storyBan) })
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // App Feature Bans
                Text("App Constraints", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                ListItem(
                    headlineContent = { Text("Download Ban") },
                    trailingContent = {
                        Switch(checked = u.downloadBan, onCheckedChange = { viewModel.toggleBan("downloadBan", u.downloadBan) })
                    }
                )
                ListItem(
                    headlineContent = { Text("P2P Sharing Ban") },
                    trailingContent = {
                        Switch(checked = u.p2pBan, onCheckedChange = { viewModel.toggleBan("p2pBan", u.p2pBan) })
                    }
                )
                ListItem(
                    headlineContent = { Text("Online Watching Ban") },
                    trailingContent = {
                        Switch(checked = u.watchBan, onCheckedChange = { viewModel.toggleBan("watchBan", u.watchBan) })
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Offline DRM Overrides
                Text("Offline DRM Overrides", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                var daysOverride by remember(u.offlineDaysOverride) { mutableStateOf(u.offlineDaysOverride?.toString() ?: "") }
                var adsOverride by remember(u.forcedAdsOverride) { mutableStateOf(u.forcedAdsOverride?.toString() ?: "") }

                OutlinedTextField(
                    value = daysOverride,
                    onValueChange = { daysOverride = it },
                    label = { Text("Offline Days Allowed") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = adsOverride,
                    onValueChange = { adsOverride = it },
                    label = { Text("Forced Ads to Unlock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.updateOfflineOverrides(daysOverride.toIntOrNull(), adsOverride.toIntOrNull())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Overrides")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
