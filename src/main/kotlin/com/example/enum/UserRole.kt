package com.example.enum

enum class UserRole {
    USER, ADMIN;

    companion object {
        infix fun enumContains(name: String): Boolean {
            return enumValues<UserRole>().any() { it.name == name }
        }
    }
}