package com.example.model

import kotlinx.serialization.Serializable

enum class Priority {
    Low, Medium, High, Vital;

    companion object {
        infix fun enumContains(name: String): Boolean {
            return enumValues<Priority>().any() { it.name == name }
        }
    }
}

@Serializable
data class Task(
    val name: String,
    val description: String,
    val priority: Priority
)