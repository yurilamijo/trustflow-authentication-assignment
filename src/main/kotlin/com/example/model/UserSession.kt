package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userId: Int,
    val username: String
)
