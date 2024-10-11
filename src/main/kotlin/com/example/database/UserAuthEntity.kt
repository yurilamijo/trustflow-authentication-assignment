package com.example.database

import com.example.model.UserAuth
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserAuthTable : IntIdTable("userAuth") {
    val userId = integer("userId").references(UserTable.id)
    val username = varchar("username", 50)
    val password = varchar("password", 255)
}

class UserAuthDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserAuthDAO>(UserAuthTable)

    var userId by UserAuthTable.userId
    var username by UserAuthTable.username
    var password by UserAuthTable.password
}

fun userAuthDAOToUserAuth(dao: UserAuthDAO): UserAuth {
    return UserAuth(dao.username, dao.password)
}