package com.example.enum

enum class Priority {
    Low, Medium, High, Vital;

    companion object {
        infix fun enumContains(name: String): Boolean {
            return enumValues<Priority>().any() { it.name == name }
        }
    }
}
