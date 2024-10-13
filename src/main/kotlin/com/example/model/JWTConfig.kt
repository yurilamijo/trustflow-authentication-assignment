package com.example.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.config.ApplicationConfig
import java.util.Date

const val JWT_CLAIM_USERNAME = "username"

object JWTConfig {
    lateinit var audience: String
    lateinit var issuer: String
    lateinit var realm: String
    lateinit var secret: String

    fun init(config: ApplicationConfig) {
        this.audience = config.property("jwt.audience").getString()
        this.issuer = config.property("jwt.issuer").getString()
        this.realm = config.property("jwt.realm").getString()
        this.secret = config.property("jwt.secret").getString()
    }

    fun init(audience: String, issuer: String, realm: String, secret: String) {
        this.audience = audience
        this.issuer = issuer
        this.realm = realm
        this.secret = secret
    }

    fun createToken(userAuth: UserAuth): String {
        return JWT.create()
            .withAudience(this.audience)
            .withIssuer(this.issuer)
            .withClaim(JWT_CLAIM_USERNAME, userAuth.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 300000))
            .sign(Algorithm.HMAC256(this.secret))
    }
}

fun JWTPrincipal.name(): String? = this.payload.getClaim(JWT_CLAIM_USERNAME).asString()

//@Serializable
//data class ApplicationConfigJWT(
//    val audience: String,
//    val issuer: String,
//    val realm: String,
//    val secret: String,
//)
//
//fun ApplicationConfig.ApplicationConfigJWT(): ApplicationConfigJWT {
//    return ApplicationConfigJWT(
//        audience = property("audience").getString(),
//        issuer = property("issuer").getString(),
//        realm = property("realm").getString(),
//        secret = property("secret").getString(),
//    )
//}