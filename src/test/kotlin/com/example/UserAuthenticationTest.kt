package com.example

import com.example.model.JWTConfig
import com.example.model.User
import com.example.model.UserAuth
import com.example.model.UserLogin
import com.example.plugins.configureDI
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSession
import com.example.repository.FakeTaskRepository
import com.example.repository.FakeUserRepository
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class UserAuthenticationTest {
    val fakeTaskRepository = FakeTaskRepository()
    val fakeUserRepository = FakeUserRepository()

    @BeforeTest
    fun startUp() {
        JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Login should succeed`() = testApplication {
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

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLogin("yurilamijo", "PasswordYuri"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "accessToken")
    }

    @Test
    fun `API call without token should fail`() = testApplication {
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

        val response = client.put("/update/{id}")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `API call with token should succeed`() = testApplication {
        application {
            configureDI()
            configureSession()
            configureSecurity()
            configureSerialization()
            configureRouting(fakeTaskRepository, fakeUserRepository)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val userAuth = UserAuth(username = "yurilamijo", password = "PasswordYuri")
        val accessToken = JWTConfig.createToken(userAuth)

        println("Generated Access Token: $accessToken") // Debug: Log the token

        val response = client.put("/update/1") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(User(1, "yuri", "lamijo", "yuri@hotmail.com", LocalDate.parse("1999-08-04")))
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
