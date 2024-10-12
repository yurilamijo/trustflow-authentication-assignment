package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAuth(
    val username: String,
    val password: String = "",
)

@Serializable
data class UserRegister(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String
)

@Serializable
data class UserLogin(
    val username: String,
    val password: String
)

@Serializable
data class UserSession(
    val token: String
)
