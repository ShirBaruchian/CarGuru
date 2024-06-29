package com.example.carguru.data.repository

import com.example.carguru.data.local.UserDao
import com.example.carguru.data.model.User
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.remote.FirebaseUserService
import com.example.carguru.utils.toUser
import com.example.carguru.utils.toUserEntity
import java.util.Date

class UserRepository(private val userDao: UserDao, private val firebaseService: FirebaseUserService) {

    suspend fun getUser(userId: String): UserEntity? {
        return userDao.getUserById(userId) ?: firebaseService.getUser(userId)?.toUserEntity()?.also { userEntity ->
            userDao.insertUser(userEntity)
        }
    }

    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
        firebaseService.saveUser(user.toUser())
    }

    suspend fun getLastUpdateDate(): Date? {
        return userDao.getLastUpdateDate()
    }

    suspend fun syncUsers() {
        val lastUpdateDate = getLastUpdateDate()
        val remoteUsers = firebaseService.getAllUsers(lastUpdateDate)
        remoteUsers.forEach { user ->
            userDao.insertUser(user.toUserEntity())
        }
    }
}
