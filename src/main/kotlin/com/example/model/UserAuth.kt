package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
    val userId: Int? = null,
    val username: String,
    val password: String = "",
)