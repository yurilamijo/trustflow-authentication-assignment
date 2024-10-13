package com.example.repository

import com.example.model.User
import com.example.model.UserAuth

interface IUserRepository {
    suspend fun getUserAuthByUsername(username: String): UserAuth?
    suspend fun doesUserAuthExistsByUsername(username: String): Boolean
    suspend fun createUser(user: User, userAuth: UserAuth): User
    suspend fun deleteUser(name: String): Boolean
    suspend fun updateUser(id: Int, user: User): User
    suspend fun getUserById(id: Int): User
}