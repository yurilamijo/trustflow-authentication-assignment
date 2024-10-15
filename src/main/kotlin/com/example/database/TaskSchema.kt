package com.example.database

import com.example.constants.COLUMN_VARCHAR_LENGTH_50
import com.example.enum.Priority
import com.example.model.Task
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

private const val TABLE_TASK_NAME = "task"
private const val TABLE_TASK_COLUMN_NAME = "name"
private const val TABLE_TASK_COLUMN_DESCRIPTION = "description"
private const val TABLE_TASK_COLUMN_PRIORITY = "priority"

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
    return Task(dao.id.value, dao.name, dao.description, Priority.valueOf(dao.priority))
}