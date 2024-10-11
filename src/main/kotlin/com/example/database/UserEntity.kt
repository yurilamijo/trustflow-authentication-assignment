package com.example.database

import com.example.model.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("user") {
    val username = varchar("username", 50)
    val password = varchar("password", 255)
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
}

fun userDAOToUser(dao: UserDAO): User {
    return User(dao.username, dao.password)
}