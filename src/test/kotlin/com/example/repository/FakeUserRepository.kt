package com.example.repository

import com.example.model.User
import com.example.model.UserAuth
import kotlinx.datetime.LocalDate

class FakeUserRepository : IUserRepository {
    private val allUser = mutableListOf<User>(
        User(1, "Yuri", "Lamijo", "yuri@hotmail.com", LocalDate.parse("2024-08-04"))
    )
    private val allUserAuth = mutableListOf<UserAuth>(
        UserAuth(1, "yurilamijo", "\$2a\$10\$5bcCcS.gLcaUQ8v84x4j.ufFf9sqytcCjGatUj4go4o9Rmv.lymq6")
    )

    override suspend fun getUserAuthByUsername(username: String): UserAuth? {
        return allUserAuth.find { it.username.equals(username, ignoreCase = true) }
    }

    override suspend fun doesUserAuthExistsByUsername(username: String): Boolean {
        return this.getUserAuthByUsername(username) is UserAuth
    }

    override suspend fun createUser(
        user: User,
        userAuth: UserAuth
    ): User {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(id: Int, user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: Int): User {
        val user = allUser.find { it.id == id }

        if (user == null) {
            throw IllegalStateException("No user found with id: '$id'")
        } else {
            return user
        }
    }
}