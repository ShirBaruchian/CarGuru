package com.example.carguru.data.model

import com.example.carguru.data.local.ReviewEntity

data class ReviewWithUser(
    val review: ReviewEntity,
    val username: String
)