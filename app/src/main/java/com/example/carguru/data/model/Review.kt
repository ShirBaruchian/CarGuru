package com.example.carguru.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Review(
    val id: String = "",
    val title: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val trim: String = "",
    val rating: Int = 0,
    val text: String = "",
    val userId: String = "",
    @ServerTimestamp val timestamp: Date? = null,
    val lastUpdated: Date? = null
)