package com.example.carguru.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val trim: String? = "",
    val rating: Int = 0,
    val text: String = "",
    val userId: String= "",
    val timestamp: Date? = Date(),
    val lastUpdated: Date = Date()
)
