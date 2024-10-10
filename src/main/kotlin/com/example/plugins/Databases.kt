package com.example.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.configureDatabases() {
    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/trustflow",
        user = "yuri",
        password = "yuri",
    )
}

suspend fun <T> dbQuery(block: Transaction.() -> T): T {
    return newSuspendedTransaction(Dispatchers.IO, statement = block)
}
