package com.example.service

import com.example.enum.Priority
import com.example.extension.UserException
import com.example.model.Task
import com.example.repository.ITaskRepository
import io.ktor.http.HttpStatusCode
import kotlin.text.isNullOrEmpty

class TaskService(private val taskRepository: ITaskRepository) : ITaskService {
    override suspend fun getAllTasks(): List<Task> {
        return taskRepository.getAllTask()
    }

    override suspend fun getTaskByName(name: String?): Task {
        name?.takeIf { it.isNotEmpty() }
            ?: throw UserException(HttpStatusCode.BadRequest, "No name was given.")

        return taskRepository.getTaskByName(name)
            ?: throw UserException(HttpStatusCode.NotFound, "Could not find any task with the name $name")
    }

    override suspend fun getAllTaskByPriority(priorityAsString: String?): List<Task> {
        if (priorityAsString.isNullOrEmpty()) {
            throw UserException(HttpStatusCode.BadRequest, "")
        } else if (Priority.enumContains(priorityAsString)) {
            val priority = Priority.valueOf(priorityAsString)
            val tasksByPriority = taskRepository.getAllTaskByPriority(priority)

            if (tasksByPriority.isEmpty()) {
                throw UserException(HttpStatusCode.NotFound, "Could not find any task with the priority $priority")
            } else {
                return tasksByPriority
            }
        } else {
            throw UserException(HttpStatusCode.BadRequest, "")
        }
    }

    override suspend fun createTask(task: Task): Task {
        return taskRepository.createTask(task)
    }

    override suspend fun deleteTask(name: String?): Boolean {
        name?.takeIf { it.isNotEmpty() }
            ?: throw UserException(HttpStatusCode.BadRequest, "No name was given.")

        return taskRepository.deleteTask(name)
            ?: throw UserException(HttpStatusCode.BadRequest, "A issue occured while deleting $name")
    }
}