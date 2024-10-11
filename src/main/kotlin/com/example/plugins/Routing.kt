package com.example.plugins

import com.example.model.JWTConfig
import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.route.taskRoute
import com.example.route.userRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
import java.time.Clock

fun Application.configureRouting(
    jwtConfig: JWTConfig,
    taskRepository: ITaskRepository = get(),
    userRepository: IUserRepository = get()
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        taskRoute(taskRepository)
        userRoute(jwtConfig, userRepository)
    }
}
