package com.example.carguru.data.repository

import android.util.Log
import com.example.carguru.data.local.UserDao
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.model.User
import com.example.carguru.data.remote.FirebaseUserService
import com.example.carguru.utils.toUser
import com.example.carguru.utils.toUserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

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
                val localUsers = userDao.getAllUsers().first()
                val usersToUpdate = users.filter { remoteUser ->
                    val localUser = localUsers.find { it.id == remoteUser.id }
                    localUser == null || localUser != remoteUser.toUserEntity()
                }
                if (usersToUpdate.isNotEmpty()) {
                    Log.d("UserRepository", "Updating users from Firebase")
                    updateLocalDatabase(usersToUpdate.map { it.toUserEntity() })
                }
                isUpdatingFromFirebase = false
            }
        }
    }

    private suspend fun updateLocalDatabase(users: List<UserEntity>) {
        userDao.clearAllUsers()
        userDao.insertUsers(users)
    }

    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
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
