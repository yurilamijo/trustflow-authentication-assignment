package com.example.route

import com.example.extension.hashPassword
import com.example.extension.verifyPassword
import com.example.model.JWTConfig
import com.example.model.User
import com.example.model.UserAuth
import com.example.model.UserLogin
import com.example.model.UserRegister
import com.example.model.UserSession
import com.example.plugins.requireSession
import com.example.repository.IUserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

private const val PARAMETER_ID = "id"

fun Routing.userRoute(userRepository: IUserRepository) {
    post("/login") {
        val (username, password) = call.receive<UserLogin>()
        val userAuth = userRepository.getUserAuthByUsername(username)

        if (userAuth != null && verifyPassword(password, userAuth.password)) {
            val accessToken = JWTConfig.createToken(userAuth)

            call.sessions.set(UserSession(userAuth.userId, username))
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "accessToken" to accessToken,
                )
            )
        } else {
            call.respond(HttpStatusCode.Forbidden, "Login failed")
            return@post
        }
    }
    post("/register") {
        var (firstName, lastName, username, password) = call.receive<UserRegister>();

        if (userRepository.doesUserAuthExistsByUsername(username)) {
            call.respond(HttpStatusCode.BadRequest, "A user with the username: $username already exists")
        } else {
            val user = User(
                id = null,
                firstName = firstName,
                lastName = lastName,
                email = null,
                dateOfBirth = null
            )
            val userAuth = UserAuth(
                username = username,
                password = hashPassword(password)
            )

            userRepository.createUser(user, userAuth)
            call.respond(HttpStatusCode.Created, "User account created with username '$username'")
        }
    }
    authenticate("jwt-auth") {
        get("/logout") {
            call.requireSession()

            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }

        put("/update/{id}") {
            call.requireSession()

            val user = call.receive<User>()
            var id = call.parameters[PARAMETER_ID]

            if (id.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Failed to update user")
                return@put
            } else {
                var updatedUser = userRepository.updateUser(id.toInt(), user)
                call.respond(HttpStatusCode.OK, updatedUser)
            }
        }

        delete("/delete/{id}") {
            call.requireSession()
            var session = call.sessions.get<UserSession>()

            var id = call.parameters[PARAMETER_ID]

            if (id.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Failed to delete user")
                return@delete
            } else {
                var updatedUser = userRepository.deleteUser(id.toInt())
                call.respond(HttpStatusCode.OK, "User with id: '$id' has been successfully deleted")
            }
        }
    }
}

