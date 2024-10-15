package com.example.model

import com.example.enum.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userId: Int,
    val username: String,
    val role: UserRole,
)
