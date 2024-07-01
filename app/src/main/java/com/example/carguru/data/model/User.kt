package com.example.carguru.data.model

import java.util.Date

data class User(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val lastUpdated: Date? = null
)