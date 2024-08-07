package com.example.carguru.utils

import java.util.Date
import com.example.carguru.data.model.*
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.local.ReviewEntity

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password,
        lastUpdated = this.lastUpdated,
        profileImageUrl = this.profileImageUrl
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password,
        lastUpdated = this.lastUpdated ?: Date(),
        profileImageUrl = this.profileImageUrl.toString()
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
        imageUrl = this.imageUrl,
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
        imageUrl = this.imageUrl,
        timestamp = this.timestamp ?: Date(),
        lastUpdated = this.lastUpdated ?: Date()
    )
}