package com.example.model

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.constants.JWT_CLAIM_USERNAME
import com.example.constants.CONFIG_PROPERTY_JWT_AUDIENCE
import com.example.constants.CONFIG_PROPERTY_JWT_ISSUER
import com.example.constants.CONFIG_PROPERTY_JWT_REALM
import com.example.constants.CONFIG_PROPERTY_JWT_SECRET
import com.example.constants.JWT_CLAIM_ROLE
import com.example.constants.JWT_CLAIM_USER_ID
import io.ktor.server.config.ApplicationConfig
import java.util.Date

object JWTConfig {
    lateinit var audience: String
    lateinit var issuer: String
    lateinit var realm: String
    lateinit var secret: String

    fun init(config: ApplicationConfig) {
        this.audience = config.property(CONFIG_PROPERTY_JWT_AUDIENCE).getString()
        this.issuer = config.property(CONFIG_PROPERTY_JWT_ISSUER).getString()
        this.realm = config.property(CONFIG_PROPERTY_JWT_REALM).getString()
        this.secret = config.property(CONFIG_PROPERTY_JWT_SECRET).getString()
    }

    fun init(audience: String, issuer: String, realm: String, secret: String) {
        this.audience = audience
        this.issuer = issuer
        this.realm = realm
        this.secret = secret
    }

    fun createToken(userAuth: UserAuth, user: User): String {
        return JWT.create()
            .withAudience(this.audience)
            .withIssuer(this.issuer)
            .withClaim(JWT_CLAIM_USER_ID, user.id)
            .withClaim(JWT_CLAIM_USERNAME, userAuth.username)
            .withClaim(JWT_CLAIM_ROLE, user.role.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 900000))
            .sign(Algorithm.HMAC256(this.secret))
    }
}