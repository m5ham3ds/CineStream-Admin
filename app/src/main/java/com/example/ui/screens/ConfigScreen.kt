package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodels.ConfigViewModel

@Composable
fun ConfigScreen(viewModel: ConfigViewModel = viewModel()) {
    val config by viewModel.config.collectAsState()

    var days by remember(config.defaultOfflineDays) { mutableStateOf(config.defaultOfflineDays.toString()) }
    var ads by remember(config.defaultForcedAds) { mutableStateOf(config.defaultForcedAds.toString()) }
    var otaJson by remember(config.providersJson) { mutableStateOf(config.providersJson) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
            Text("Offline DRM Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("These settings apply to all users unless specifically overridden in their profile.", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = days,
                onValueChange = { days = it },
                label = { Text("Default Offline Days") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ads,
                onValueChange = { ads = it },
                label = { Text("Default Forced Ads") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.updateDrmSettings(days.toIntOrNull() ?: 2, ads.toIntOrNull() ?: 5)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update DRM Config")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            Text("Dynamic OTA Updates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Update scrapers, providers, or endpoints globally for the main CineStream app in real-time.", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otaJson,
                onValueChange = { otaJson = it },
                label = { Text("Providers JSON") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.updateOtaJson(otaJson) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Deploy OTA Update")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
    }
}
