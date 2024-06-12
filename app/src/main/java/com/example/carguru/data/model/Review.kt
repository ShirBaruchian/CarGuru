package com.example.carguru.data.model

data class Review (
    val id: String,
    val carId: String,
    val userId: String,
    val rating: Int,
    val imageUrl: String?
)