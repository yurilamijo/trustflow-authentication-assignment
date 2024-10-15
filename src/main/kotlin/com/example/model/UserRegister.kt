package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRegister(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String
)
