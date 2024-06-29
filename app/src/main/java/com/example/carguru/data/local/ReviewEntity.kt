package com.example.carguru.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val manufacturer: String,
    val model: String,
    val year: String,
    val trim: String,
    val rating: Int,
    val text: String,
    val userId: String,
    val timestamp: Date?
)
