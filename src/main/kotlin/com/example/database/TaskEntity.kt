package com.example.database

import com.example.model.Priority
import com.example.model.Task
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

const val TABLE_TASK_NAME = "task"
const val TABLE_TASK_COLUMN_NAME = "name"
const val TABLE_TASK_COLUMN_DESCRIPTION = "description"
const val TABLE_TASK_COLUMN_PRIORITY = "priority"
const val COLUMN_VARCHAR_LENGTH_50 = 50

object TaskTable : IntIdTable(TABLE_TASK_NAME) {
    val name = varchar(TABLE_TASK_COLUMN_NAME, COLUMN_VARCHAR_LENGTH_50)
    val description = varchar(TABLE_TASK_COLUMN_DESCRIPTION, COLUMN_VARCHAR_LENGTH_50)
    val priority = varchar(TABLE_TASK_COLUMN_PRIORITY, COLUMN_VARCHAR_LENGTH_50)
}

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)

    var name by TaskTable.name
    var description by TaskTable.description
    var priority by TaskTable.priority
}

fun taskDAOToTask(dao: TaskDAO): Task {
    return Task(dao.name, dao.description, Priority.valueOf(dao.priority))
}