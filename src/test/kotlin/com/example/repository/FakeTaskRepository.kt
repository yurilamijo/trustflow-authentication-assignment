package com.example.repository

import com.example.enum.Priority
import com.example.model.Task

class FakeTaskRepository : ITaskRepository {
    private val allTask = mutableListOf<Task>(
        Task(1, "Coding", "Coding a project", Priority.VITAL),
        Task(2, "Bouldering","Time to go bouldering", Priority.LOW),
        Task(3, "Cooking", "Cooking a meal", Priority.MEDIUM),
        Task(4, "Cleaning", "Clean the house", Priority.MEDIUM),
    )

    override suspend fun getAllTask(): List<Task> {
        return allTask
    }

    override suspend fun getAllTaskByPriority(priority: Priority): List<Task> {
        return allTask.filter { it.priority == priority }
    }

    override suspend fun getTaskByName(name: String): Task? {
        return allTask.find { it.name.equals(name, ignoreCase = true) }
    }

    override suspend fun createTask(task: Task): Task {
        if (getTaskByName(task.name) == null) {
            allTask.add(task)

            return task
        } else {
            throw IllegalStateException("Unable to create a duplicate task.")
        }
    }

    override suspend fun deleteTask(name: String): Boolean {
        return allTask.removeIf { it.name == name }
    }
}