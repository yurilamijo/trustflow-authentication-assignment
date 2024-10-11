package com.example.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val dateOfBirth: LocalDateTime? = null
)
