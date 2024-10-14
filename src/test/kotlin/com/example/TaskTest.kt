package com.example

import com.example.enum.Priority
import com.example.model.JWTConfig
import com.example.model.Task
import com.example.model.UserLogin
import com.example.plugins.configureDI
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSession
import com.example.repository.FakeTaskRepository
import com.example.repository.FakeUserRepository
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin
import kotlin.test.*

const val TEST_VALUE_TASK_COOKING = "Cooking"
const val TEST_VALUE_TASK_CLEANING = "Cleaning"
const val TEST_VALUE_TASK_PADEL = "Padel"

class TaskTest {
    private lateinit var token_jwt: String
    private lateinit var token_session: String

    val fakeTaskRepository = FakeTaskRepository()
    val fakeUserRepository = FakeUserRepository()

    @BeforeTest
    fun startUp() = testApplication {
        JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")
        application {
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(userRepository = fakeUserRepository)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response: HttpResponse = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLogin(TEST_VALUE_USERNAME, TEST_VALUE_PASSWORD))
        }
        var responseBody = response.body<UserLoginBody>()

        token_jwt = responseBody.accessToken
        token_session = response.headers.get(HEADER_TRUSTFLOW_SESSION).toString()
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
        application {
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(fakeTaskRepository)
        }

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
