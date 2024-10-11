package com.example.repository

import com.example.database.TaskDAO
import com.example.database.TaskTable
import com.example.database.taskDAOToTask
import com.example.model.Priority
import com.example.model.Task
import com.example.plugins.dbQuery
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TaskRepository : ITaskRepository {
    override suspend fun getAllTask(): List<Task> {
        return dbQuery { TaskDAO.all().map(::taskDAOToTask) }
    }

    override suspend fun getAllTaskByPriority(priority: Priority): List<Task> {
        return dbQuery {
            TaskDAO.find { (TaskTable.priority eq priority.toString()) }
                .map(::taskDAOToTask)
        }
    }

    override suspend fun getTaskByName(name: String): Task? {
        return dbQuery {
            TaskDAO.find { (TaskTable.name eq name) }
                .limit(1)
                .map(::taskDAOToTask)
                .firstOrNull()
        }
    }

    override suspend fun createTask(task: Task): Task {
        return taskDAOToTask(
            dbQuery {
                TaskDAO.new {
                    name = task.name
                    description = task.description
                    priority = task.priority.toString()
                }
            }
        )
    }

    override suspend fun deleteTask(name: String): Boolean {
        return dbQuery {
            TaskTable.deleteWhere {
                TaskTable.name eq name
            } > 0
        }
    }
}