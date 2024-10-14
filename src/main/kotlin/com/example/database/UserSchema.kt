package com.example.database

import com.example.constants.COLUMN_VARCHAR_LENGTH_50
import com.example.model.User
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

private const val TABLE_USER_NAME = "user"
private const val TABLE_USER_COLUMN_FIRSTNAME = "firstName"
private const val TABLE_USER_COLUMN_LASTNAME = "lastName"
private const val TABLE_USER_COLUMN_EMAIL = "email"
private const val TABLE_USER_COLUMN_DATE_OF_BIRTH = "dateOfBirth"
private const val COLUMN_VARCHAR_LENGTH_100 = 100

object UserTable : IntIdTable(TABLE_USER_NAME) {
    val firstName = varchar(TABLE_USER_COLUMN_FIRSTNAME, COLUMN_VARCHAR_LENGTH_50)
    val lastName = varchar(TABLE_USER_COLUMN_LASTNAME, COLUMN_VARCHAR_LENGTH_50)
    val email = varchar(TABLE_USER_COLUMN_EMAIL, COLUMN_VARCHAR_LENGTH_100).nullable()
    val dateOfBirth = date(TABLE_USER_COLUMN_DATE_OF_BIRTH).nullable()
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var firstName by UserTable.firstName
    var lastName by UserTable.lastName
    var email by UserTable.email
    var dateOfBirth by UserTable.dateOfBirth
}

fun userDAOToUser(dao: UserDAO): User {
    val dateOfBirth: LocalDate? = if (dao.dateOfBirth == null) null else LocalDate.parse(dao.dateOfBirth.toString())
    return User(dao.id.value, dao.firstName, dao.lastName, dao.email, dateOfBirth)
}