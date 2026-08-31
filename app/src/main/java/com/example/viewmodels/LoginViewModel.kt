package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isResetLoading: Boolean = false,
    val resetMessage: String? = null,
    val resetError: String? = null
)

class LoginViewModel : ViewModel() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    
    private val _uiState = MutableStateFlow(LoginUiState(
        isLoggedIn = try { FirebaseAuth.getInstance().currentUser != null } catch (e: Exception) { false }
    ))
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

    fun signInWithGoogleToken(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Google Sign-in failed"
                    )
                }
            }
    }

    fun setGoogleSignInLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }

    fun setError(error: String) {
        _uiState.value = _uiState.value.copy(error = error, isLoading = false)
    }

    fun clearResetMessages() {
        _uiState.value = _uiState.value.copy(resetMessage = null, resetError = null)
    }

    fun resetPassword(email: String) {
        val emailTrimmed = email.trim()
        if (emailTrimmed.isEmpty()) {
            _uiState.value = _uiState.value.copy(resetError = "Please enter your email address")
            return
        }

        _uiState.value = _uiState.value.copy(isResetLoading = true, resetError = null, resetMessage = null)

        auth.sendPasswordResetEmail(emailTrimmed)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isResetLoading = false,
                        resetMessage = "Password reset email sent"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isResetLoading = false,
                        resetError = task.exception?.message ?: "Failed to send reset email"
                    )
                }
            }
    }
    
    fun logout() {
        auth.signOut()
        _uiState.value = LoginUiState(isLoggedIn = false)
    }
}
