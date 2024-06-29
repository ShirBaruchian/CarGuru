package com.example.carguru.utils

import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.local.UserEntity
import com.example.carguru.models.Review
import com.example.carguru.models.User

fun ReviewEntity.toReview(): Review {
    return Review(
        id = this.id.toString(),
        title = this.title,
        manufacturer = this.manufacturer,
        model = this.model,
        year = this.year,
        trim = this.trim,
        rating = this.rating,
        text = this.text,
        userId = this.userId,
        timestamp = this.timestamp
    )
}

fun Review.toReviewEntity(): ReviewEntity {
    return ReviewEntity(
        id = this.id.toInt(),
        title = this.title,
        manufacturer = this.manufacturer,
        model = this.model,
        year = this.year,
        trim = this.trim,
        rating = this.rating,
        text = this.text,
        userId = this.userId,
        timestamp = this.timestamp
    )
}

fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password
    )
}

fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        password = this.password
    )
}
