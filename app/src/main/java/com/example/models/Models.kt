package com.example.models

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val lastLoginTimestamp: Long = 0L,
    val isPremium: Boolean = false,
    val chatBan: Boolean = false,
    val storyBan: Boolean = false,
    val downloadBan: Boolean = false,
    val p2pBan: Boolean = false,
    val watchBan: Boolean = false,
    val offlineDaysOverride: Int? = null,
    val forcedAdsOverride: Int? = null
)

data class GlobalConfig(
    val defaultOfflineDays: Int = 2,
    val defaultForcedAds: Int = 5,
    val providersJson: String = "{}"
)

data class NotificationRequest(
    val targetType: TargetType = TargetType.ALL,
    val targetUid: String = "",
    val title: String = "",
    val message: String = ""
) {
    enum class TargetType {
        ALL, UID
    }
}
