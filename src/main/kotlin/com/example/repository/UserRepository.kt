package com.example.repository

import com.example.database.UserAuthDAO
import com.example.database.UserAuthTable
import com.example.database.UserTable
import com.example.database.userAuthDAOToUserAuth
import com.example.model.User
import com.example.model.UserAuth
import com.example.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId

class UserRepository : IUserRepository {
    override suspend fun getUserAuthByUsername(username: String): UserAuth? {
        return dbQuery {
            UserAuthDAO.find { (UserAuthTable.username eq username) }
                .limit(1)
                .map(::userAuthDAOToUserAuth)
                .firstOrNull()
        }
    }

    override suspend fun createUser(user: User, userAuth: UserAuth): User {
        dbQuery {
            val userId = UserTable.insertAndGetId {
                it[UserTable.firstName] = user.firstName
                it[UserTable.lastName] = user.lastName
            }
            UserAuthTable.insert {
                it[UserAuthTable.userId] = userId.value
                it[UserAuthTable.username] = userAuth.username
                it[UserAuthTable.password] = userAuth.password
            }
        }

        // TODO: Check this code as this is kinda weird
        return user
    }

    override suspend fun deleteUser(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun doesUserAuthExistsByUsername(username: String): Boolean {
        return this.getUserAuthByUsername(username) is UserAuth
    }
}