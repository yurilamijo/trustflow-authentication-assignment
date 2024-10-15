package com.example.model

import com.example.enum.Priority
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int = -1,
    val name: String,
    val description: String,
    val priority: Priority
)