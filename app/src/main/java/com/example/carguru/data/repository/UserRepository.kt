package com.example.carguru.data.repository

import java.util.Date
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.carguru.utils.toUser
import kotlinx.coroutines.CoroutineScope
import com.example.carguru.data.local.UserDao
import com.example.carguru.utils.toUserEntity
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.remote.FirebaseUserService

class UserRepository(
    private val userDao: UserDao,
    private val firebaseUserService: FirebaseUserService
) {
    @Volatile
    private var isUpdatingFromFirebase = false

    fun startListeningForUpdates() {
        firebaseUserService.addUserListener { users ->
            CoroutineScope(Dispatchers.IO).launch {
                isUpdatingFromFirebase = true
                Log.d("UserRepository", "Updating users from Firebase")
                updateLocalDatabase(users.map { it.toUserEntity() })
                isUpdatingFromFirebase = false
            }
        }
    }

    private suspend fun updateLocalDatabase(users: List<UserEntity>) {
        userDao.clearAllUsers()
        userDao.insertUsers(users)
    }

    fun getUser(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    suspend fun saveUser(user: UserEntity) {
        if (!isUpdatingFromFirebase) {
            Log.d("UserRepository", "Saving user locally")
            userDao.insertUser(user)
            firebaseUserService.updateUser(user.toUser())
        }
    }

    suspend fun syncUsers(scope: CoroutineScope) = withContext(Dispatchers.IO) {
        Log.d("UserRepository", "Syncing users")
        val lastUpdateDate = userDao.getLatestUpdateDate() ?: Date(0) // Default to epoch if no date

        val updatedUsers = firebaseUserService.getUsersUpdatedAfter(lastUpdateDate)

        if (updatedUsers.isNotEmpty()) {
            isUpdatingFromFirebase = true
            userDao.insertUsers(updatedUsers.map { it.toUserEntity() })
            isUpdatingFromFirebase = false
        }
    }
}
