package com.example.route

import com.example.enum.Priority
import com.example.extension.authorized
import com.example.model.Task
import com.example.plugins.checkUserRole
import com.example.plugins.requireSession
import com.example.repository.ITaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.text.isNullOrEmpty

private const val PARAMETER_NAME = "name"
private const val PARAMETER_PRIORITY = "priority"

fun Routing.taskRoute(taskRepository: ITaskRepository) {
    authenticate("jwt-auth") {
        authorized("USER", "ADMIN") {
            route("/tasks") {
                get {
                    call.requireSession()

                    val tasks = taskRepository.getAllTask()
                    call.respond(tasks)
                }

                get("/byName/{name}") {
                    call.requireSession()

                    val name = call.pathParameters[PARAMETER_NAME]

                    if (name.isNullOrEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "No name was given.")
                    } else {
                        val task =
                            taskRepository.getTaskByName(name) ?: return@get call.respond(HttpStatusCode.NotFound)

                        call.respond(task)
                    }
                }

                get("/byPriority/{priority}") {
                    call.requireSession()

                    val priorityAsString = call.pathParameters[PARAMETER_PRIORITY]

                    if (priorityAsString.isNullOrEmpty()) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else if (Priority.enumContains(priorityAsString)) {
                        val priority = Priority.valueOf(priorityAsString)
                        val tasksByPriority = taskRepository.getAllTaskByPriority(priority)

                        if (tasksByPriority.isEmpty()) {
                            call.respond(HttpStatusCode.NotFound)
                        } else {
                            call.respond(tasksByPriority)
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                post {
                    call.requireSession()

                    try {
                        val task = call.receive<Task>()
                        taskRepository.createTask(task)
                        call.respond(HttpStatusCode.Created, task)
                    } catch (exception: Exception) {
                        when (exception) {
                            is IllegalStateException, is JsonConvertException -> call.respond(HttpStatusCode.BadRequest)
                            else -> throw exception
                        }
                    }
                }

                delete("/{name}") {
                    call.requireSession()
                    call.checkUserRole("ADMIN")

                    val name = call.parameters[PARAMETER_NAME]

                    if (name.isNullOrEmpty()) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else if (taskRepository.deleteTask(name)) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}