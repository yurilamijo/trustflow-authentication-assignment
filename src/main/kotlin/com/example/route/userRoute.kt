package com.example.route

import com.example.extension.authorized
import com.example.model.User
import com.example.model.UserSession
import com.example.plugins.requireSession
import com.example.repository.IUserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

private const val PARAMETER_ID = "id"

fun Routing.userRoute(userRepository: IUserRepository) {
    authenticate("jwt-auth") {
        authorized("USER", "ADMIN") {
            route("/user") {
                put("/update/{id}") {
                    call.requireSession()

                    val user = call.receive<User>()
                    var id = call.parameters[PARAMETER_ID]

                    if (id.isNullOrEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "Failed to update user")
                        return@put
                    } else {
                        var updatedUser = userRepository.updateUser(id.toInt(), user)
                        call.respond(HttpStatusCode.OK, updatedUser)
                    }
                }

                delete("/delete/{id}") {
                    call.requireSession()

                    var session = call.sessions.get<UserSession>()

                    var id = call.parameters[PARAMETER_ID]

                    if (id.isNullOrEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, "Failed to delete user")
                        return@delete
                    } else {
                        var updatedUser = userRepository.deleteUser(id.toInt())
                        call.respond(HttpStatusCode.OK, "User with id: '$id' has been successfully deleted")
                    }
                }
            }
        }
    }
}

