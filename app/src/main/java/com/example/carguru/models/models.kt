package com.example.carguru.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CarDetails(
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val trim: String = "",
    val imageUrl: String = ""
)

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
    @ServerTimestamp val timestamp: Date? = null
)

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = ""
)

data class ReviewWithUser(
    val review: Review,
    val username: String
)