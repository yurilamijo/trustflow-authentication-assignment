package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.JWTConfig
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {
    authentication {
        jwt() {
            realm = JWTConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JWTConfig.secret))
                    .withAudience(JWTConfig.audience)
                    .withIssuer(JWTConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(JWTConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { scheme, realm ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token to access ${HttpHeaders.WWWAuthenticate} $scheme realm=\"$realm\" is either invalid or expired."
                )
            }
        }
    }
}