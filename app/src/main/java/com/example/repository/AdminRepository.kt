package com.example.repository

import com.example.models.GlobalConfig
import com.example.models.NotificationRequest
import com.example.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AdminRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val configCollection = firestore.collection("config")
    private val notificationsCollection = firestore.collection("notifications")

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val listener = usersCollection.orderBy("lastLoginTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val users = snapshot?.documents?.mapNotNull { it.toObject(User::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    fun getUser(userId: String): Flow<User?> = callbackFlow {
        val listener = usersCollection.document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val user = snapshot?.toObject(User::class.java)?.copy(id = snapshot.id)
            trySend(user)
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any?>) {
        usersCollection.document(userId).update(updates).await()
    }

    fun getGlobalConfig(): Flow<GlobalConfig> = callbackFlow {
        val listener = configCollection.document("global").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val config = snapshot?.toObject(GlobalConfig::class.java) ?: GlobalConfig()
            trySend(config)
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateGlobalConfig(updates: Map<String, Any?>) {
        configCollection.document("global").update(updates).await()
    }

    suspend fun sendNotification(request: NotificationRequest) {
        notificationsCollection.add(request).await()
    }
}
