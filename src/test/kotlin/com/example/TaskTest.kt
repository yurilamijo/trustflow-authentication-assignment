package com.example

import com.example.enum.Priority
import com.example.model.JWTConfig
import com.example.model.Task
import com.example.plugins.configureDI
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSession
import com.example.repository.FakeTaskRepository
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin
import kotlin.test.*

const val TEST_VALUE_TASK_COOKING = "Cooking"
const val TEST_VALUE_TASK_CLEANING = "Cleaning"
const val TEST_VALUE_TASK_PADEL = "Padel"

class TaskTest {
    val fakeTaskRepository = FakeTaskRepository()

    @BeforeTest
    fun startUp() {
        JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Test Task filter by priority is successful`() = testApplication {
        application {
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(fakeTaskRepository)
        }
        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Medium")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<List<Task>>()
        val expectedTaskNames = listOf(TEST_VALUE_TASK_COOKING, TEST_VALUE_TASK_CLEANING)
        val actualTaskNames = responseBody.map(Task::name)
        assertContentEquals(expectedTaskNames, actualTaskNames)
    }

    @Test
    fun `Test Task filter with invalid priority fails`() = testApplication {
        application {
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(fakeTaskRepository)
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
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(fakeTaskRepository)
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

        assertContains(taskNames, TEST_VALUE_TASK_PADEL)
    }
}
