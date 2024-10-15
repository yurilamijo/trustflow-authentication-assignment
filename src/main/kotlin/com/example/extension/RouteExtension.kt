package com.example.extension

import com.example.plugins.RoleBasedAuthorizationPlugin
import io.ktor.server.routing.Route

fun Route.authorized(
    vararg hasAnyRole: String,
    build: Route.() -> Unit
) {
    install(RoleBasedAuthorizationPlugin) { roles = hasAnyRole.toSet() }
    build()
}