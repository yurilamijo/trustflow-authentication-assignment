package com.example.service

import com.example.model.Task

interface ITaskService {
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskByName(name: String?): Task
    suspend fun getAllTaskByPriority(priorityAsString: String?): List<Task>
    suspend fun createTask(task: Task): Task
    suspend fun deleteTask(name: String?): Boolean
}