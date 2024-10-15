package com.example.repository

import com.example.enum.UserRole
import com.example.model.User
import com.example.model.UserAuth
import kotlinx.datetime.LocalDate

class FakeUserRepository : IUserRepository {
    private val allUser = mutableListOf<User>(
        User(1, "Yuri", "Lamijo", "yuri@hotmail.com", LocalDate.parse("1999-08-04"), UserRole.ADMIN),
        User(2, "Robbert", "Jan", "robbert@hotmail.com", LocalDate.parse("1980-08-04"), UserRole.USER)
    )
    private val allUserAuth = mutableListOf<UserAuth>(
        UserAuth(1, "YuriLam", "\$2a\$10\$5bcCcS.gLcaUQ8v84x4j.ufFf9sqytcCjGatUj4go4o9Rmv.lymq6"),
        UserAuth(2, "RobbertJan", "\$2a\$10\$5bcCcS.gLcaUQ8v84x4j.ufFf9sqytcCjGatUj4go4o9Rmv.lymq6")
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
        val userIdIncremented = allUser.last().id.plus(1)
        val userAuthIdIncremented = allUser.last().id.plus(1)

        allUser.add(
            User(
                id = userIdIncremented,
                firstName = user.firstName,
                lastName = user.lastName,
                email = "",
                dateOfBirth = null,
                role = user.role,
            )
        )
        allUserAuth.add(
            UserAuth(
                userId = userAuthIdIncremented,
                username = userAuth.username,
                password = userAuth.password
            )
        )

        return getUserById(userIdIncremented)
    }

    override suspend fun deleteUser(userId: Int): Boolean {
        allUser.removeIf { it.id == userId }
        allUserAuth.removeIf { it.userId == userId }

        return true
    }

    override suspend fun updateUser(userId: Int, user: User): User {
        return user
    }

    override suspend fun getUserById(userId: Int): User {
        val user = allUser.find { it.id == userId }

        if (user == null) {
            throw IllegalStateException("No user found with id: '$userId'")
        } else {
            return user
        }
    }
}