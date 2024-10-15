package com.example.model

import com.example.enum.UserRole
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = -1,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val dateOfBirth: LocalDate?,
    val role: UserRole = UserRole.USER
)
