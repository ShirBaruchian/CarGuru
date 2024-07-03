package com.example.carguru.data.local

import java.util.Date
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT MAX(lastUpdated) FROM users")
    suspend fun getLatestUpdateDate(): Date?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("DELETE FROM users WHERE id NOT IN (:userIds)")
    suspend fun deleteUsersNotIn(userIds: List<String>)
}
