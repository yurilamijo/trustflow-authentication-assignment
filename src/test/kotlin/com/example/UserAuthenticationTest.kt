package com.example

import com.example.model.JWTConfig
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
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@Serializable
data class AccessToken (
    val accessToken: String
)

class UserAuthenticationTest {
    val fakeTaskRepository = FakeTaskRepository()
    val fakeUserRepository = FakeUserRepository()

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `login should succeed with token`() = testApplication {
        application {
            JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")

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
            JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")

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

        val response = client.get("/tasks")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `API call with token should succeed`() = testApplication {
        application {
            JWTConfig.init("jwt-audience", "jwt-issuer", "ktor sample app", "test-secret-123")

            configureDI()
            configureSerialization()
            configureSecurity()
            configureSession()
            configureRouting(fakeTaskRepository, fakeUserRepository)
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val userAuth = UserAuth(username = "yurilamijo", password = "PasswordYuri")
        val accessToken = JWTConfig.createToken(userAuth)
        val response = client.get("/tasks") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
