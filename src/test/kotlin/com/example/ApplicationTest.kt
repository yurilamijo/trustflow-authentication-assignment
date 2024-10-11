package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.jwtConfig
import com.example.repository.FakeTaskRepository
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun `Test Task filter by priority is successful`() = testApplication {
        application {
            val taskRepository = FakeTaskRepository()
            val jwtConfig = environment.config.config("jwt").jwtConfig()

            configureSerialization()
            configureRouting(jwtConfig, taskRepository)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Medium")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<List<Task>>()
        val expectedTaskNames = listOf("Cooking", "Cleaning")
        val actualTaskNames = responseBody.map(Task::name)
        assertContentEquals(expectedTaskNames, actualTaskNames)
    }

    @Test
    fun `Test Task filter with invalid priority fails`() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }


    @Test
    fun `Test Task filter without result`() = testApplication {
        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `Test Task creation`() = testApplication {
        application {
            val taskRepository = FakeTaskRepository()
            val jwtConfig = environment.config.config("jwt").jwtConfig()

            configureSerialization()
            configureRouting(jwtConfig, taskRepository)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val task = Task("Padel", "Time to go padel", Priority.Low)
        val responseCreation = client.post("/tasks") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.Json
            )

            setBody(task)
        }
        assertEquals(HttpStatusCode.Created, responseCreation.status)

        val response = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, response.status)

        val taskNames = response
            .body<List<Task>>()
            .map { it.name }

        assertContains(taskNames, "Padel")
    }
}
