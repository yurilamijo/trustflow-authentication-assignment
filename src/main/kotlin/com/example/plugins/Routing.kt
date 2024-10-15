package com.example.plugins

import com.example.extension.UserException
import com.example.route.taskRoute
import com.example.route.userAuthRoute
import com.example.route.userRoute
import com.example.service.ITaskService
import com.example.service.IUserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting(
    taskService: ITaskService = get(),
    userService: IUserService = get(),
) {
    install(StatusPages) {
        exception<UserException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = cause.httpStatusCode)

        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        taskRoute(taskService)
        userAuthRoute(userService)
        userRoute(userService)
    }
}
