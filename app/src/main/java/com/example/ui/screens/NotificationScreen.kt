package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.models.NotificationRequest
import com.example.viewmodels.NotificationViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(viewModel: NotificationViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Send Notification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Target Audience", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.targetType == NotificationRequest.TargetType.ALL,
                    onClick = { viewModel.updateTargetType(NotificationRequest.TargetType.ALL) }
                )
                Text("All Users", modifier = Modifier.padding(start = 8.dp))
                
                Spacer(modifier = Modifier.width(24.dp))
                
                RadioButton(
                    selected = state.targetType == NotificationRequest.TargetType.UID,
                    onClick = { viewModel.updateTargetType(NotificationRequest.TargetType.UID) }
                )
                Text("Specific UID", modifier = Modifier.padding(start = 8.dp))
            }

            if (state.targetType == NotificationRequest.TargetType.UID) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.targetUid,
                    onValueChange = viewModel::updateTargetUid,
                    label = { Text("User UID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Notification Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.message,
                onValueChange = viewModel::updateMessage,
                label = { Text("Notification Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.sendNotification()
                    scope.launch {
                        snackbarHostState.showSnackbar("Notification queued for sending.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.title.isNotBlank() && state.message.isNotBlank() && 
                          (state.targetType == NotificationRequest.TargetType.ALL || state.targetUid.isNotBlank())
            ) {
                Text("Send Push Notification")
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
