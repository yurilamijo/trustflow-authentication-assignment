package com.example.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.engine.applicationEnvironment
import java.util.Date

const val JWT_CLAIM_USERNAME = "username"

object JWTConfig {
    val audience = applicationEnvironment().config.config("jwt-audience").toString()
    val issuer = applicationEnvironment().config.config("jwt-issuer").toString()
    val realm = applicationEnvironment().config.config("jwt-realm").toString()
    val secret = applicationEnvironment().config.config("jwt-secret").toString()

    fun createToken(userAuth: UserAuth): String {
        return JWT.create()
            .withAudience(this.audience)
            .withIssuer(this.issuer)
            .withClaim(JWT_CLAIM_USERNAME, userAuth.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 300000))
            .sign(Algorithm.HMAC256(this.secret))
    }

//    fun verifyToken(token: String): UserAuth? =
//        try {
//            val jwt = JWT.require(Algorithm.HMAC256(this.secret))
//                .withAudience(this.audience)
//                .withIssuer(this.issuer)
//                .build()
//                .verify(token)
//            jwt.getClaim(JWT_CLAIM_USERNAME).asString()?.let { name ->
//                UserAuth(username = name)
//            }
//        } catch (e: JWTVerificationException) {
//            null
//        }
}

fun JWTPrincipal.name(): String? = this.payload.getClaim(JWT_CLAIM_USERNAME).asString()

