package com.example

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import com.example.enum.Priority
import com.example.model.Task
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

private const val TEST_VALUE_USERNAME = "YuriLam"
private const val TEST_VALUE_PASSWORD = "PasswordYuri"
private const val TEST_VALUE_TASK_COOKING = "Cooking"
private const val TEST_VALUE_TASK_CLEANING = "Cleaning"
private const val TEST_VALUE_TASK_PADEL = "Padel"

class TaskTest : BaseApplicationTest() {
    @Test
    fun `Test Task filter by priority is successful`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.get("/tasks/byPriority/Medium") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<List<Task>>()
        val expectedTaskNames = listOf(TEST_VALUE_TASK_COOKING, TEST_VALUE_TASK_CLEANING)
        val actualTaskNames = responseBody.map(Task::name)
        assertContentEquals(expectedTaskNames, actualTaskNames)
    }

    @Test
    fun `Test Task filter with invalid priority fails`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.get("/tasks/byPriority/Invalid") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }


    @Test
    fun `Test Task filter without result`() = testApplication {
        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `Test Task creation`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val task = Task("Padel", "Time to go padel", Priority.Low)
        val responseCreation = client.post("/tasks") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
            setBody(task)
        }
        assertEquals(HttpStatusCode.Created, responseCreation.status)
    }
}
