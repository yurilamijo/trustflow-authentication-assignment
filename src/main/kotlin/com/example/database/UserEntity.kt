package com.example.database

import com.example.model.User
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object UserTable : IntIdTable("user") {
    val firstName = varchar("firstName", 50)
    val lastName = varchar("lastName", 50)
    val email = varchar("email", 100).nullable()
    val dateOfBirth = date("dateOfBirth").nullable()
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var email by UserTable.email
    var dateOfBirth by UserTable.dateOfBirth
}

fun userDAOToUser(dao: UserDAO): User {
    val dateOfBirth: LocalDateTime? =
        if (dao.dateOfBirth == null) null else LocalDateTime.parse(dao.dateOfBirth.toString())
    return User(dao.firstName, dao.lastName, dao.email, dateOfBirth)
}