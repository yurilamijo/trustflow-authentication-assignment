package com.example.route

import com.example.extension.hashPassword
import com.example.extension.verifyPassword
import com.example.model.JWTConfig
import com.example.model.User
import com.example.model.UserAuth
import com.example.model.createToken
import com.example.model.verify
import com.example.repository.IUserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Register(val firstName: String, val lastName: String, val username: String, val password: String)

@Serializable
data class Login(val username: String, val password: String)

@Serializable
data class RefreshToken(val token: String)

fun Routing.userRoute(jwtConfig: JWTConfig, userRepository: IUserRepository) {
    post("/login") {
        val (username, password) = call.receive<Login>()
        val user = userRepository.getUserAuthByUsername(username)

        if (user != null && verifyPassword(password, user.password)) {
            fun createToken(expirationSeconds: Long): String =
                jwtConfig.createToken(user, expirationSeconds)

            val accessToken = createToken(jwtConfig.expirationInSeconds.accessToken)
            val refreshToken = createToken(jwtConfig.expirationInSeconds.refreshToken)
            call.respond(
                mapOf(
                    "accessToken" to accessToken,
                    "refreshToken" to refreshToken
                )
            )
        } else {
            call.respond(HttpStatusCode.Forbidden, "Login failed")
            return@post
        }
    }
    post("/register") {
        var (firstName, lastName, username, password) = call.receive<Register>();

        if (userRepository.doesUserAuthExistsByUsername(username)) {
            call.respond(HttpStatusCode.BadRequest, "A user with the username: $username already exists")
        } else {
            val user = User (
                firstName = firstName,
                lastName = lastName
            )
            val userAuth = UserAuth(
                username = username,
                password = hashPassword(password)
            )

            userRepository.createUser(user, userAuth)
            call.respond(HttpStatusCode.Created, "User account created with username '$username'")
        }
    }
    post("/refresh") {
        val refreshToken = call.receive<RefreshToken>()

        val user = jwtConfig.verify(refreshToken.token) ?: run {
            call.respond(HttpStatusCode.Forbidden, "Invalid refresh token")
            return@post
        }

        val newAccessToken = jwtConfig.createToken(user, jwtConfig.expirationInSeconds.accessToken)
        val newRefreshToken = jwtConfig.createToken(user, jwtConfig.expirationInSeconds.refreshToken)

        call.respond(
            mapOf(
                "accessToken" to newAccessToken,
                "refreshToken" to newRefreshToken
            )
        )
    }
    authenticate("auth-jwt") {
        get("/hello") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("name").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())

            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}

