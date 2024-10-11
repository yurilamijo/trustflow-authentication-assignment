package com.example.repository

import com.example.database.UserDAO
import com.example.database.UserTable
import com.example.database.userDAOToUser
import com.example.model.User
import com.example.plugins.dbQuery

class UserRepository : IUserRepository {
    override suspend fun getUserByUsername(username: String): User? {
        return dbQuery {
            UserDAO.find { (UserTable.username eq username) }
                .limit(1)
                .map(::userDAOToUser)
                .firstOrNull()
        }
    }

    override suspend fun createUser(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(name: String): Boolean {
        TODO("Not yet implemented")
    }
}