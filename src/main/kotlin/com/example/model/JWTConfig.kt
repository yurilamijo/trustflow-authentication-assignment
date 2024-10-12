package com.example.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

const val JWT_CLAIM_NAME = "name"

data class JWTConfig(
    val audience: String,
    val issuer: String,
    val realm: String,
    val secret: String,
    val expirationInSeconds: ExpirationInSecondsConfig
) {
    data class ExpirationInSecondsConfig(
        val accessToken: Long,
        val refreshToken: Long
    )
}

fun JWTConfig.createToken(userAuth: UserAuth, expirationInSeconds: Long): String {
    return JWT.create()
        .withAudience(this.audience)
        .withIssuer(this.issuer)
        .withClaim(JWT_CLAIM_NAME, userAuth.username)
        .withExpiresAt(Date(System.currentTimeMillis() + 300000))
        .sign(Algorithm.HMAC256(this.secret))
}

fun JWTConfig.verify(token: String): UserAuth? =
    try {
        val jwt = JWT.require(Algorithm.HMAC256(this.secret))
            .withAudience(this.audience)
            .withIssuer(this.issuer)
            .build()
            .verify(token)
        jwt.getClaim(JWT_CLAIM_NAME).asString()?.let { name ->
            UserAuth(name)
        }
    } catch (e: JWTVerificationException) {
        null
    }