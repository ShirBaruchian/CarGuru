package com.example.carguru.models

import com.example.carguru.data.local.ReviewEntity

data class ReviewWithUser(
    val review: ReviewEntity,
    val username: String
)