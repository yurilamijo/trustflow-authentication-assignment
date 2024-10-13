package com.example

import com.example.model.JWTConfig
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val config = environment.config
    JWTConfig.init(config)

    configureDI()
    configureSession()
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
