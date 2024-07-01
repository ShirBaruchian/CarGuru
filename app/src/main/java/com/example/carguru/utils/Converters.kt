package com.example.carguru.utils

import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.model.*
import java.util.Date

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password,
        lastUpdated = this.lastUpdated
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password,
        lastUpdated = this.lastUpdated ?: Date()
    )
}

fun ReviewEntity.toReview(): Review {
    return Review(
        id = this.id,
        title = this.title,
        manufacturer = this.manufacturer,
        model = this.model,
        year = this.year,
        trim = this.trim,
        rating = this.rating,
        text = this.text,
        userId = this.userId,
        timestamp = this.timestamp,
        lastUpdated = this.lastUpdated
    )
}

fun Review.toReviewEntity(): ReviewEntity {
    return ReviewEntity(
        id = this.id,
        title = this.title,
        manufacturer = this.manufacturer,
        model = this.model,
        year = this.year,
        trim = this.trim,
        rating = this.rating,
        text = this.text,
        userId = this.userId,
        timestamp = this.timestamp ?: Date(),
        lastUpdated = this.lastUpdated ?: Date()
    )
}