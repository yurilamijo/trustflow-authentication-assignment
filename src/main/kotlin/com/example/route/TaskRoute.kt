package com.example.route

import com.example.enum.Priority
import com.example.model.Task
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

const val PARAMETER_NAME = "name"
const val PARAMETER_PRIORITY = "priority"

fun Routing.taskRoute(taskRepository: ITaskRepository) {
    route("/tasks") {
        get {
            val tasks = taskRepository.getAllTask()
            call.respond(tasks)
        }

        get("/byName/{name}") {
            val nameAsString = call.pathParameters[PARAMETER_NAME].takeIf { it.isNullOrEmpty() == false }
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest
                )

            val task =
                taskRepository.getTaskByName(nameAsString) ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(task)
        }

        get("/byPriority/{priority}") {
            val priorityAsString = call.pathParameters[PARAMETER_PRIORITY]

            if (priorityAsString.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            } else if (Priority.enumContains(priorityAsString)) {
                val priority = Priority.valueOf(priorityAsString)
                val tasksByPriority = taskRepository.getAllTaskByPriority(priority)

                if (tasksByPriority.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                } else {
                    call.respond(tasksByPriority)
                }
            } else {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
        }

        authenticate("jwt-auth") {
            post {
                try {
                    val task = call.receive<Task>()
                    taskRepository.createTask(task)
                    call.respond(HttpStatusCode.Created, task)
                } catch (ex: Exception) {
                    when (ex) {
                        is IllegalStateException, is JsonConvertException -> call.respond(HttpStatusCode.BadRequest)
                        else -> throw ex
                    }
                }
            }

            delete("/{name}") {
                val name = call.parameters[PARAMETER_NAME]

                if (name.isNullOrEmpty()) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                } else if (taskRepository.deleteTask(name)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                    return@delete
                }
            }
        }
    }
}