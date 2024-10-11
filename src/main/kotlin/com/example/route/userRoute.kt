package com.example.route

import com.example.model.JWTConfig
import com.example.model.createToken
import com.example.model.verify
import com.example.plugins.name
import com.example.repository.IUserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Login(val username: String, val password: String)

@Serializable
data class RefreshToken(val token: String)

fun Routing.userRoute(jwtConfig: JWTConfig, userRepository: IUserRepository) {
    post("/login") {
        val login = call.receive<Login>()
        val user = userRepository.getUserByUsername(login.username)

        if (login.username == user?.username && login.password == user?.password) {
            // Continue
        } else {
            call.respond(HttpStatusCode.Forbidden, "Login failed")
            return@post
        }

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
    }
    post("/refresh") {
        // Extract the refresh token from the request
        val refreshToken = call.receive<RefreshToken>()

        // Verify the refresh token and obtain the user
        val user = jwtConfig.verify(refreshToken.token) ?: run {
            call.respond(HttpStatusCode.Forbidden, "Invalid refresh token")
            return@post
        }

        // Create new access and refresh tokens for the user
        val newAccessToken = jwtConfig.createToken(user, jwtConfig.expirationInSeconds.accessToken)
        val newRefreshToken = jwtConfig.createToken(user, jwtConfig.expirationInSeconds.refreshToken)

        // Respond with the new tokens
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
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())

            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}