package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.GlobalConfig
import com.example.models.NotificationRequest
import com.example.models.User
import com.example.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val allUsers = repository.getAllUsers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val users: StateFlow<List<User>> = combine(allUsers, _searchQuery) { users, query ->
        if (query.isBlank()) users else users.filter { it.username.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUsers = allUsers.map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    // Consider active if logged in within the last 30 days (approx)
    val activeUsers = allUsers.map { userList -> 
        val threshold = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        userList.count { it.lastLoginTimestamp >= threshold }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inactiveUsers = combine(totalUsers, activeUsers) { total, active -> total - active }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

class UserDetailViewModel(
    private val userId: String,
    private val repository: AdminRepository = AdminRepository()
) : ViewModel() {
    val user: StateFlow<User?> = repository.getUser(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun togglePremium() {
        val currentUser = user.value ?: return
        viewModelScope.launch {
            repository.updateUser(userId, mapOf("isPremium" to !currentUser.isPremium))
        }
    }

    fun toggleBan(banType: String, currentValue: Boolean) {
        viewModelScope.launch {
            repository.updateUser(userId, mapOf(banType to !currentValue))
        }
    }

    fun updateOfflineOverrides(days: Int?, ads: Int?) {
        viewModelScope.launch {
            repository.updateUser(userId, mapOf(
                "offlineDaysOverride" to days,
                "forcedAdsOverride" to ads
            ))
        }
    }
}

class ConfigViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    val config: StateFlow<GlobalConfig> = repository.getGlobalConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GlobalConfig())

    fun updateDrmSettings(days: Int, ads: Int) {
        viewModelScope.launch {
            repository.updateGlobalConfig(mapOf(
                "defaultOfflineDays" to days,
                "defaultForcedAds" to ads
            ))
        }
    }

    fun updateOtaJson(json: String) {
        viewModelScope.launch {
            repository.updateGlobalConfig(mapOf("providersJson" to json))
        }
    }
}

class NotificationViewModel(private val repository: AdminRepository = AdminRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) { _uiState.value = _uiState.value.copy(title = title) }
    fun updateMessage(msg: String) { _uiState.value = _uiState.value.copy(message = msg) }
    fun updateTargetType(type: NotificationRequest.TargetType) { _uiState.value = _uiState.value.copy(targetType = type) }
    fun updateTargetUid(uid: String) { _uiState.value = _uiState.value.copy(targetUid = uid) }

    fun sendNotification() {
        val state = _uiState.value
        if (state.title.isBlank() || state.message.isBlank()) return
        
        viewModelScope.launch {
            repository.sendNotification(
                NotificationRequest(
                    targetType = state.targetType,
                    targetUid = if (state.targetType == NotificationRequest.TargetType.UID) state.targetUid else "",
                    title = state.title,
                    message = state.message
                )
            )
            // Reset after sending
            _uiState.value = NotificationState()
        }
    }
}

data class NotificationState(
    val title: String = "",
    val message: String = "",
    val targetType: NotificationRequest.TargetType = NotificationRequest.TargetType.ALL,
    val targetUid: String = ""
)
