package com.example.repository

import com.example.model.User

interface IUserRepository {
    suspend fun getUserByUsername(username: String): User?
    suspend fun createUser(user: User): User
    suspend fun deleteUser(name: String): Boolean
    suspend fun doesUserExistsByUsername(username: String): Boolean
}