package com.example.repository

import com.example.enum.Priority
import com.example.model.Task

interface ITaskRepository {
    suspend fun getAllTask(): List<Task>
    suspend fun getAllTaskByPriority(priority: Priority): List<Task>
    suspend fun getTaskByName(name: String): Task?
    suspend fun createTask(task: Task): Task
    suspend fun deleteTask(name: String): Boolean
}