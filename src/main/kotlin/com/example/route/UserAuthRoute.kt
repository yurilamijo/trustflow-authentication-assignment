package com.example.route

import com.example.constants.RESPONSE_FIELD_ACCESS_TOKEN
import com.example.model.UserLogin
import com.example.model.UserRegister
import com.example.model.UserSession
import com.example.plugins.requireSession
import com.example.service.IUserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Routing.userAuthRoute(userService: IUserService) {
    post("/login") {
        val userLogin = call.receive<UserLogin>()
        val userSession = userService.userLogin(userLogin)

        call.sessions.set(userSession)
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                RESPONSE_FIELD_ACCESS_TOKEN to userSession.accessToken,
            )
        )
    }
    post("/register") {
        var userRegister = call.receive<UserRegister>();
        var user = userService.userRegister(userRegister)
        call.respond(
            HttpStatusCode.Created,
            "User account for ${user.firstName} was created with username '${userRegister.username}'"
        )
    }
    authenticate("jwt-auth") {
        get("/logout") {
            call.requireSession()
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        }
    }
}

