package com.example

import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwtConfig = environment.config.config("jwt").jwtConfig()

    configureDI()
    configureSession()
    configureSecurity(jwtConfig)
    configureSerialization()
    configureDatabases()
    configureRouting(jwtConfig)
}
