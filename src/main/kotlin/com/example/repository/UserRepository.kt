package com.example.repository

import com.example.database.UserAuthDAO
import com.example.database.UserAuthTable
import com.example.database.UserDAO
import com.example.database.UserTable
import com.example.database.userAuthDAOToUserAuth
import com.example.database.userDAOToUser
import com.example.enum.UserRole
import com.example.model.User
import com.example.model.UserAuth
import com.example.plugins.dbQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDate

class UserRepository : IUserRepository {
    override suspend fun getUserById(userId: Int): User {
        var userDao = dbQuery {
            UserDAO.findById(userId)
        }

        if (userDao == null) {
            throw IllegalStateException("No user found with id: '$userId'")
        } else {
            return userDAOToUser(userDao)
        }
    }

    override suspend fun getAllUserByRole(userRole: UserRole): List<User> {
        return dbQuery {
            UserDAO.find {(UserTable.role eq userRole.toString())}.map(::userDAOToUser)
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
        lateinit var userId: EntityID<Int>

        dbQuery {
            userId = UserTable.insertAndGetId {
                it[UserTable.firstName] = user.firstName
                it[UserTable.lastName] = user.lastName
                it[UserTable.role] = UserRole.USER.toString()
            }
            UserAuthTable.insert {
                it[UserAuthTable.userId] = userId.value
                it[UserAuthTable.username] = userAuth.username
                it[UserAuthTable.password] = userAuth.password
            }
        }

        return getUserById(userId.value)
    }

    override suspend fun deleteUser(userId: Int): Boolean {
        var query = dbQuery {
            UserAuthTable.deleteWhere { UserAuthTable.userId eq userId }
            UserTable.deleteWhere { UserTable.id eq userId }
        }
        println(query.toString())

        return true
    }

    override suspend fun updateUser(userId: Int, user: User): User {
        dbQuery {
            UserTable.update({ UserTable.id eq userId }) {
                it[UserTable.firstName] = user.firstName
                it[UserTable.lastName] = user.lastName
                it[UserTable.email] = user.email
                it[UserTable.dateOfBirth] = LocalDate.parse(user.dateOfBirth.toString())
            }
        }

        return getUserById(userId)
    }

    override suspend fun doesUserAuthExistsByUsername(username: String): Boolean {
        return this.getUserAuthByUsername(username) is UserAuth
    }
}