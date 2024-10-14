package com.example.plugins

import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.route.taskRoute
import com.example.route.UserAuthRoute
import com.example.route.UserRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    taskRepository: ITaskRepository = get(),
    userRepository: IUserRepository = get(),
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        taskRoute(taskRepository)
        UserAuthRoute(userRepository)
        UserRoute(userRepository)
    }
}
