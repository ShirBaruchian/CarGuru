package com.example.carguru.models

data class CarDetails(
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val trim: String = "",
    val imageUrl: String = ""
)

data class Review(
    val id: String,
    val userId: String,
    val title: String,
    val carDetails: CarDetails = CarDetails(),
    val rating: Int = 0,
    val text: String
)

data class ReviewWithUser(
    val review: Review,
    val username: String
)