package com.example.carguru.data.repository

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

class UserRepository(
    private val userDao: UserDao,
    private val firebaseUserService: FirebaseUserService
) {

    fun startListeningForUpdates(scope: CoroutineScope) {
        firebaseUserService.addUserListener { users ->
            scope.launch {
                withContext(Dispatchers.IO) {
                    updateLocalDatabase(users.map { it.toUserEntity() })
                }
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
        userDao.insertUser(user)
        firebaseUserService.updateUser(user.toUser())
    }

    suspend fun syncUsers(scope: CoroutineScope) = withContext(Dispatchers.IO) {
        val localUsers = userDao.getAllUsers().first()
        val remoteUsers = firebaseUserService.getAllUsers()

        val localUserMap = localUsers.associateBy { it.id }
        val remoteUserMap = remoteUsers.associateBy { it.id }

        // Update or insert users from Firebase into Room
        for (remoteUser in remoteUsers) {
            val localUser = localUserMap[remoteUser.id]
            if (localUser == null || localUser.lastUpdated.before(remoteUser.lastUpdated)) {
                userDao.insertUser(remoteUser.toUserEntity())
            }
        }

        // Update or insert users from Room into Firebase
        for (localUser in localUsers) {
            val remoteUser = remoteUserMap[localUser.id]
            if (remoteUser == null || remoteUser.lastUpdated?.before(localUser.lastUpdated) != false) {
                firebaseUserService.updateUser(localUser.toUser())
            }
        }

        // Identify and delete users from Room that are no longer in Firebase
        val remoteUserIds = remoteUsers.map { it.id }.toSet()
        val usersToDeleteFromRoom = localUsers.filter { it.id !in remoteUserIds }
        for (userToDelete in usersToDeleteFromRoom) {
            userDao.deleteUserById(userToDelete.id)
        }

        // Identify and delete users from Firebase that are no longer in Room
        val localUserIds = localUsers.map { it.id }.toSet()
        val usersToDeleteFromFirebase = remoteUsers.filter { it.id !in localUserIds }
        for (userToDelete in usersToDeleteFromFirebase) {
            firebaseUserService.deleteUser(userToDelete.id)
        }
    }
}
