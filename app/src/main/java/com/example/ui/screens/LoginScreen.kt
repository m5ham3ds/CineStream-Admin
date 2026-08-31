package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.viewmodels.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val webClientId = context.getString(R.string.default_web_client_id)

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Admin Login",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Sign in to access the dashboard",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        label = { Text("Admin Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = uiState.error != null
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        isError = uiState.error != null,
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = "Toggle password visibility")
                            }
                        }
                    )

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::login,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login")
                        }
                    }

                    OutlinedButton(
                        onClick = viewModel::signUp,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading && uiState.email.isNotBlank() && uiState.password.isNotBlank()
                    ) {
                        Text("Sign Up")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(text = "OR", style = MaterialTheme.typography.labelMedium)
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.setGoogleSignInLoading(true)
                            coroutineScope.launch {
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(webClientId)
                                        .setAutoSelectEnabled(true)
                                        .build()

                                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )

                                    val credential = result.credential
                                    if (credential is CustomCredential &&
                                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        viewModel.signInWithGoogleToken(googleIdTokenCredential.idToken)
                                    } else {
                                        viewModel.setError("Unexpected credential type.")
                                    }
                                } catch (e: GetCredentialException) {
                                    viewModel.setError(e.message ?: "Google Sign In failed")
                                } catch (e: Exception) {
                                    viewModel.setError(e.message ?: "Google Sign In failed")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text("Sign in with Google")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showForgotPasswordDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Forgot Password?")
                    }
                }
            }
        }
        
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    showForgotPasswordDialog = false
                    viewModel.clearResetMessages()
                },
                title = { Text("Reset Password") },
                text = {
                    Column {
                        Text("Enter your email address to receive a password reset link.")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        if (uiState.isResetLoading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        if (uiState.resetError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.resetError!!, color = MaterialTheme.colorScheme.error)
                        }
                        if (uiState.resetMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(uiState.resetMessage!!, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.resetPassword(resetEmail) },
                        enabled = !uiState.isResetLoading
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showForgotPasswordDialog = false
                        viewModel.clearResetMessages()
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
