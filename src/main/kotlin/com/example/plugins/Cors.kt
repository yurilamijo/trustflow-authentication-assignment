package com.example.plugins

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        anyHost()

        // Headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.WWWAuthenticate)
        allowHeader(HEADER_TRUSTFLOW_SESSION)
        exposeHeader(HEADER_TRUSTFLOW_SESSION)
        exposeHeader(HttpHeaders.Authorization)

        // Methods
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)

        allowCredentials = true
    }
}