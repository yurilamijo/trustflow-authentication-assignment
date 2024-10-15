package com.example.database

import com.example.constants.COLUMN_VARCHAR_LENGTH_225
import com.example.constants.COLUMN_VARCHAR_LENGTH_50
import com.example.model.UserAuth
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

private const val TABLE_USER_AUTH_NAME = "userAuth"
private const val TABLE_USER_AUTH_COLUMN_USER_ID = "userId"
private const val TABLE_USER_AUTH_COLUMN_USERNAME = "username"
private const val TABLE_USER_AUTH_COLUMN_PASSWORD = "password"

object UserAuthTable : IntIdTable(TABLE_USER_AUTH_NAME) {
    val userId = integer(TABLE_USER_AUTH_COLUMN_USER_ID).references(UserTable.id)
    val username = varchar(TABLE_USER_AUTH_COLUMN_USERNAME, COLUMN_VARCHAR_LENGTH_50)
    val password = varchar(TABLE_USER_AUTH_COLUMN_PASSWORD, COLUMN_VARCHAR_LENGTH_225)
}

class UserAuthDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserAuthDAO>(UserAuthTable)

    var userId by UserAuthTable.userId
    var username by UserAuthTable.username
    var password by UserAuthTable.password
}

fun userAuthDAOToUserAuth(dao: UserAuthDAO): UserAuth {
    return UserAuth(dao.userId, dao.username, dao.password)
}