package com.example.repository

import com.example.model.User
import com.example.model.UserAuth

interface IUserRepository {
    suspend fun getUserAuthByUsername(username: String): UserAuth?
    suspend fun doesUserAuthExistsByUsername(username: String): Boolean
    suspend fun createUser(user: User, userAuth: UserAuth): User
    suspend fun deleteUser(userId: Int): Boolean
    suspend fun updateUser(userId: Int, user: User): User
    suspend fun getUserById(userId: Int): User
}