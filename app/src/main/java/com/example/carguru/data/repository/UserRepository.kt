package com.example.carguru.data.repository

import androidx.lifecycle.LiveData
import com.example.carguru.data.local.AppDatabase
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.remote.FirebaseUserService
import com.example.carguru.models.User
import com.example.carguru.utils.toUser
import com.example.carguru.utils.toUserEntity

class UserRepository(private val database: AppDatabase, private val remoteService: FirebaseUserService) {
    private val userDao = database.userDao()

    fun getUserById(userId: String): LiveData<UserEntity> = userDao.getUserById(userId)

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
        remoteService.syncUserWithFirestore(user.toUser())
    }

    suspend fun fetchUserFromFirestore(userId: String): UserEntity? {
        return remoteService.fetchUserFromFirestore(userId)?.toUserEntity()?.also {
            userDao.insertUser(it)
        }
    }

    suspend fun fetchUsersFromFirestore() {
        val users = remoteService.fetchUsersFromFirestore()
        userDao.insertUsers(users.map { it.toUserEntity() })
    }
}
