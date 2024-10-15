package com.example.route

import com.example.extension.authorized
import com.example.model.User
import com.example.plugins.requireSession
import com.example.service.IUserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

private const val PARAMETER_ID = "id"

fun Routing.userRoute(userService: IUserService) {
    authenticate("jwt-auth") {
        authorized("USER", "ADMIN") {
            route("/user") {
                get("/{id}") {
                    var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()

                    call.respond(HttpStatusCode.OK, userService.getUserById(userId))
                }

                put("/update/{id}") {
                    var session = call.requireSession()
                    val user = call.receive<User>()
                    var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()
                    val updatedUser = userService.updateUser(userId, user, session.userId, session.role)

                    call.respond(HttpStatusCode.OK, updatedUser)
                }

                delete("/delete/{id}") {
                    var session = call.requireSession()
                    var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()

                    userService.deleteUser(userId, session.userId, session.role)

                    call.respond(HttpStatusCode.NoContent, "User with id: '$userId' has been successfully deleted.")
                }
            }
        }
    }
}

