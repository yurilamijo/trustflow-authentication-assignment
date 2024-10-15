package com.example.route

import com.example.extension.authorized
import com.example.model.Task
import com.example.plugins.checkUserRole
import com.example.plugins.requireSession
import com.example.service.ITaskService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

private const val PARAMETER_NAME = "name"
private const val PARAMETER_PRIORITY = "priority"

fun Routing.taskRoute(taskService: ITaskService) {
    authenticate("jwt-auth") {
        authorized("USER", "ADMIN") {
            route("/tasks") {
                get {
                    call.requireSession()
                    call.respond(HttpStatusCode.OK, taskService.getAllTasks())
                }

                get("/byName/{name}") {
                    call.requireSession()
                    val name = call.pathParameters[PARAMETER_NAME]

                    call.respond(HttpStatusCode.OK, taskService.getTaskByName(name))
                }

                get("/byPriority/{priority}") {
                    call.requireSession()
                    val priorityAsString = call.pathParameters[PARAMETER_PRIORITY]

                    call.respond(HttpStatusCode.OK, taskService.getAllTaskByPriority(priorityAsString))
                }

                post {
                    call.requireSession()
                    val task = call.receive<Task>()

                    call.respond(HttpStatusCode.Created, taskService.createTask(task))
                }

                delete("/{name}") {
                    call.requireSession()
                    call.checkUserRole("ADMIN")
                    val name = call.parameters[PARAMETER_NAME]

                    call.respond(HttpStatusCode.NoContent, taskService.deleteTask(name))
                }
            }
        }
    }
}