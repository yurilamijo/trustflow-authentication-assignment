package com.example.route

import com.example.constants.ERROR_RESPONSE_KEY
import com.example.enum.UserRole
import com.example.extension.UserException
import com.example.extension.authorized
import com.example.model.User
import com.example.plugins.requireSession
import com.example.repository.IUserRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val PARAMETER_ID = "id"

fun Routing.userRoute(userRepository: IUserRepository) {
    authenticate("jwt-auth") {
        authorized("USER", "ADMIN") {
            route("/user") {
                get("/{id}") {
                    try {
                        var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()

                        if (userId == null) {
                            throw UserException(
                                HttpStatusCode.BadRequest,
                                "Failed to retrieve the user, no user id was given."
                            )
                        } else {
                            var user = userRepository.getUserById(userId)
                            call.respond(HttpStatusCode.OK, user)
                        }
                    } catch (userException: UserException) {
                        call.respond(userException.httpStatusCode, mapOf(ERROR_RESPONSE_KEY to userException.message))
                    } catch (exception: Exception) {
                        throw exception
                    }
                }

                put("/update/{id}") {
                    try {
                        var session = call.requireSession()
                        val user = call.receive<User>()
                        var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()

                        if (userId == null) {
                            throw UserException(HttpStatusCode.BadRequest, "Failed to update the user, no user id was given.")
                        } else if ((session.userId == userId && session.role == UserRole.USER) || session.role == UserRole.ADMIN) {
                            var updatedUser = userRepository.updateUser(userId, user)
                            call.respond(HttpStatusCode.OK, updatedUser)
                        } else {
                            throw UserException(HttpStatusCode.Unauthorized, "You can only update your own account.")
                        }
                    } catch (userException: UserException) {
                        call.respond(userException.httpStatusCode, mapOf(ERROR_RESPONSE_KEY to userException.message))
                    } catch (exception: Exception) {
                        throw exception
                    }
                }

                delete("/delete/{id}") {
                    try {
                        var session = call.requireSession()
                        var userId = call.parameters[PARAMETER_ID]?.toIntOrNull()

                        if (userId == null) {
                            throw UserException(HttpStatusCode.BadRequest, "Failed to delete the user, no user id was given.")
                        } else if ((session.userId == userId && session.role == UserRole.USER) || session.role == UserRole.ADMIN) {
                            userRepository.deleteUser(userId)
                            call.respond(HttpStatusCode.NoContent, "User with id: '$userId' has been successfully deleted.")
                        } else {
                            throw UserException(HttpStatusCode.Unauthorized, "You can only delete your own account.")
                        }
                    } catch (userException: UserException) {
                        call.respond(userException.httpStatusCode, mapOf(ERROR_RESPONSE_KEY to userException.message))
                    } catch (exception: Exception) {
                        throw exception
                    }
                }
            }
        }
    }
}

