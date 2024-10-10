package com.example

import com.example.plugins.*
import com.example.repository.TaskRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSecurity()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
