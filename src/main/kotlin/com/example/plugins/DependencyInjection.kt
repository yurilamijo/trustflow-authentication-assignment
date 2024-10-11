package com.example.plugins

import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule = module {
    single<ITaskRepository> {
        TaskRepository()
    }
    single<IUserRepository> {
        UserRepository()
    }
}

fun Application.configureDI() {
    install(Koin) {
        modules(appModule)
    }
}