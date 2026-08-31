package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        try {
            _uiState.value = _uiState.value.copy(
                isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkAndCreateUserProfile(auth.currentUser)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = task.exception?.message ?: "Login failed"
                        )
                    }
                }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Login Error: ${e.message}"
            )
        }
    }

    fun signInWithGoogleToken(idToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkAndCreateUserProfile(auth.currentUser)
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

    private fun checkAndCreateUserProfile(user: FirebaseUser?) {
        if (user == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "User is null")
            return
        }

        try {
            val firestore = FirebaseFirestore.getInstance()
            val userRef = firestore.collection("users").document(user.uid)

            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // Profile exists, update last login and proceed
                        userRef.update("lastLoginTimestamp", System.currentTimeMillis())
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null
                        )
                    } else {
                        // Profile doesn't exist, create default profile
                        val newUser = User(
                            id = user.uid,
                            username = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                            email = user.email ?: "",
                            lastLoginTimestamp = System.currentTimeMillis()
                        )
                        userRef.set(newUser, SetOptions.merge())
                            .addOnCompleteListener { setTask ->
                                if (setTask.isSuccessful) {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        isLoggedIn = true,
                                        error = null
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = setTask.exception?.message ?: "Failed to create profile"
                                    )
                                }
                            }
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = task.exception?.message ?: "Failed to check profile"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Profile Error: ${e.message}"
            )
        }
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

        try {
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
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isResetLoading = false,
                resetError = "Reset Error: ${e.message}"
            )
        }
    }
    
    fun logout() {
        auth.signOut()
        _uiState.value = LoginUiState(isLoggedIn = false)
    }
}
