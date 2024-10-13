package com.example.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int?,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val dateOfBirth: LocalDate?
)
