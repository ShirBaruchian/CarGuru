package com.example.carguru.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val lastUpdated: Date = Date(),
    val profileImageUrl: String = ""
)
