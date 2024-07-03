package com.example.carguru.data.model

import java.util.Date
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    val id: String = "",
    val title: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val trim: String? = "",
    val rating: Int = 0,
    val text: String = "",
    val userId: String = "",
    val imageUrl: String? = "",
    @ServerTimestamp val timestamp: Date? = null,
    val lastUpdated: Date? = null
)