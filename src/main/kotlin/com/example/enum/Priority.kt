package com.example.enum

enum class Priority {
    LOW, MEDIUM, HIGH, VITAL;

    companion object {
        infix fun enumContains(name: String): Boolean {
            return enumValues<Priority>().any() { it.name == name }
        }
    }
}
