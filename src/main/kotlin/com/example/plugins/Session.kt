package com.example.plugins

import com.example.model.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.directorySessionStorage
import io.ktor.server.sessions.get
import io.ktor.server.sessions.header
import io.ktor.server.sessions.sessions
import io.ktor.util.hex
import java.io.File

fun Application.configureSession() {
    install(Sessions) {
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        header<UserSession>("trustflow_session", directorySessionStorage(File("build/.sessions"))) {
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
}

suspend fun ApplicationCall.requireSession() {
    val session = sessions.get<UserSession>()

    if (session == null ) {
        respond(HttpStatusCode.BadRequest, "Invalid request, please login again as your session doesn't exists or has been expired.")
    } else {
        // Continue
    }
}