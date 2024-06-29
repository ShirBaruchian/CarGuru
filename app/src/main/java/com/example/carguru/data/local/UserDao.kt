package com.example.carguru.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.carguru.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}
