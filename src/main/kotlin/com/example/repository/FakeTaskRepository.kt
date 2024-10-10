package com.example.repository

import com.example.model.Priority
import com.example.model.Task

class FakeTaskRepository : ITaskRepository {
    private val tasks = mutableListOf<Task>(
        Task("Coding", "Coding a project", Priority.Vital),
        Task("Bouldering", "Time to go bouldering", Priority.Low),
        Task("Cooking", "Cooking a meal", Priority.Medium),
        Task("Cleaning", "Clean the house", Priority.Medium),
    )

    override suspend fun getAllTask(): List<Task> {
        return tasks
    }

    override suspend fun getAllTaskByPriority(priority: Priority): List<Task> {
        return tasks.filter { it.priority == priority }
    }

    override suspend fun getTaskByName(name: String): Task? {
        return tasks.find { it.name.equals(name, ignoreCase = true) }
    }

    override suspend fun createTask(task: Task): Task {
        if (getTaskByName(task.name) == null) {
            tasks.add(task)

            return task
        } else {
            throw IllegalStateException("Unable to create a duplicate task.")
        }
    }

    override suspend fun deleteTask(name: String): Boolean {
        return tasks.removeIf { it.name == name }
    }
}