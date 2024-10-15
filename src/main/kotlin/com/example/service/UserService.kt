package com.example.service

import com.example.enum.UserRole
import com.example.extension.UserException
import com.example.extension.hashPassword
import com.example.extension.verifyPassword
import com.example.model.JWTConfig
import com.example.model.User
import com.example.model.UserAuth
import com.example.model.UserLogin
import com.example.model.UserRegister
import com.example.model.UserSession
import com.example.repository.IUserRepository
import io.ktor.http.HttpStatusCode

class UserService(private val userRepository: IUserRepository) : IUserService {
    override suspend fun userLogin(userLogin: UserLogin): UserSession {
        val (username, password) = userLogin
        val userAuth = userRepository.getUserAuthByUsername(username)

        if (userAuth != null && verifyPassword(password, userAuth.password)) {
            val user = userRepository.getUserById(userAuth.userId)
            val accessToken = JWTConfig.createToken(userAuth, user)

            return UserSession(userAuth.userId, username, user.role, accessToken)
        } else {
            throw UserException(HttpStatusCode.Forbidden, "Login failed.")
        }
    }

    override suspend fun userRegister(userRegister: UserRegister): User {
        var (firstName, lastName, username, password) = userRegister;

        if (userRepository.doesUserAuthExistsByUsername(username)) {
            throw UserException(HttpStatusCode.BadRequest, "A user with the username: $username already exists.")
        } else {
            val user = User(
                firstName = firstName,
                lastName = lastName,
                email = null,
                dateOfBirth = null
            )
            val userAuth = UserAuth(
                username = username,
                password = hashPassword(password)
            )

            return userRepository.createUser(user, userAuth)
        }
    }

    override suspend fun getUserById(userId: Int?): User {
        if (userId == null) {
            throw UserException(
                HttpStatusCode.BadRequest,
                "Failed to retrieve the user, no user id was given."
            )
        } else {
            return userRepository.getUserById(userId)
        }
    }

    override suspend fun getAllUserByRole(userRoleAsString: String?): List<User> {
        if (userRoleAsString.isNullOrEmpty()) {
            throw UserException(HttpStatusCode.BadRequest, "The user role parameter cannot be empty.")
        } else if (UserRole.enumContains(userRoleAsString)) {
            val userRole = UserRole.valueOf(userRoleAsString)
            val allUserWithUserRoleUser = userRepository.getAllUserByRole(userRole)

            if (allUserWithUserRoleUser.isEmpty()) {
                throw UserException(HttpStatusCode.NotFound, "Could not find any users with the user role $userRoleAsString.")
            } else {
                return allUserWithUserRoleUser
            }
        } else {
            throw UserException(HttpStatusCode.BadRequest, "The given user role is unknown.")
        }
    }

    override suspend fun updateUser(userId: Int?, user: User, sessionUserId: Int, sessionRole: UserRole): User {
        if (userId == null) {
            throw UserException(
                HttpStatusCode.BadRequest,
                "Failed to update the user, no user id was given."
            )
        } else if ((sessionUserId == userId && sessionRole == UserRole.USER) || sessionRole == UserRole.ADMIN) {
            return userRepository.updateUser(userId, user)
        } else {
            throw UserException(HttpStatusCode.Unauthorized, "You can only update your own account.")
        }
    }

    override suspend fun deleteUser(userId: Int?, sessionUserId: Int, sessionRole: UserRole): Boolean {
        if (userId == null) {
            throw UserException(HttpStatusCode.BadRequest, "Failed to delete the user, no user id was given.")
        } else if (sessionUserId == userId && sessionRole == UserRole.USER || sessionRole == UserRole.ADMIN) {
            return userRepository.deleteUser(userId)
        } else {
            throw UserException(HttpStatusCode.Unauthorized, "You can only delete your own account.")
        }
    }
}