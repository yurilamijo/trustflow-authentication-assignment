package com.example.plugins

import com.example.repository.ITaskRepository
import com.example.repository.IUserRepository
import com.example.repository.TaskRepository
import com.example.repository.UserRepository
import com.example.service.ITaskService
import com.example.service.IUserService
import com.example.service.TaskService
import com.example.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val serviceModule = module {
    single<ITaskService> { TaskService(get()) }
    single<IUserService> { UserService(get()) }
}

val repositoryModule = module {
    single<ITaskRepository> { TaskRepository() }
    single<IUserRepository> { UserRepository() }
}

fun Application.configureDI() {
    install(Koin) {
        modules(repositoryModule, serviceModule)
    }
}