package com.example.service

import com.example.enum.UserRole
import com.example.model.User
import com.example.model.UserLogin
import com.example.model.UserRegister
import com.example.model.UserSession

interface IUserService {
    suspend fun userLogin(userLogin: UserLogin): UserSession
    suspend fun userRegister(userRegister: UserRegister): User
    suspend fun updateUser(userId: Int?, user: User, sessionUserId: Int, sessionRole: UserRole): User
    suspend fun deleteUser(userId: Int?, sessionUserId: Int, sessionRole: UserRole): Boolean
    suspend fun getUserById(userId: Int?): User
}