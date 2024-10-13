package com.example.repository

import com.example.database.UserAuthDAO
import com.example.database.UserAuthTable
import com.example.database.UserDAO
import com.example.database.UserTable
import com.example.database.userAuthDAOToUserAuth
import com.example.database.userDAOToUser
import com.example.model.User
import com.example.model.UserAuth
import com.example.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class UserRepository : IUserRepository {
    override suspend fun getUserById(id: Int): User {
        var userDao = dbQuery {
            UserDAO.findById(id)
        }

        if (userDao == null) {
            throw IllegalStateException("No user found with id: '$id'")
        } else {
            return userDAOToUser(userDao)
        }
    }

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

    override suspend fun updateUser(id: Int, user: User): User {
        dbQuery {
            UserTable.update({ UserTable.id eq id }) {
                it[UserTable.firstName] = user.firstName
                it[UserTable.lastName] = user.lastName
                it[UserTable.email] = user.email
                it[UserTable.dateOfBirth] = LocalDate.parse(user.dateOfBirth.toString())
            }
        }

        return getUserById(id)
    }

    override suspend fun doesUserAuthExistsByUsername(username: String): Boolean {
        return this.getUserAuthByUsername(username) is UserAuth
    }
}