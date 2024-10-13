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

const val RESPONSE_FIELD_ACCESS_TOKEN = "accessToken"

const val TEST_VALUE_USER_ID = 1
const val TEST_VALUE_USERNAME = "yurilamijo"
const val TEST_VALUE_PASSWORD = "PasswordYuri"
const val TEST_VALUE_FIRSTNAME = "Yuri"
const val TEST_VALUE_LASTNAME = "Lamijo"
const val TEST_VALUE_EMAIL = "yuri@test.nl"
const val TEST_VALUE_DATE_OF_BIRTH = "1999-04-08"

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
            setBody(UserLogin(TEST_VALUE_USERNAME, TEST_VALUE_PASSWORD))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), RESPONSE_FIELD_ACCESS_TOKEN)
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

        val userAuth = UserAuth(username = TEST_VALUE_USERNAME, password = TEST_VALUE_PASSWORD)
        val accessToken = JWTConfig.createToken(userAuth)

        println("Generated Access Token: $accessToken") // Debug: Log the token

        val response = client.put("/update/1") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                contentType(ContentType.Application.Json)
                setBody(
                    User(
                        TEST_VALUE_USER_ID,
                        TEST_VALUE_FIRSTNAME,
                        TEST_VALUE_LASTNAME,
                        TEST_VALUE_EMAIL,
                        LocalDate.parse(TEST_VALUE_DATE_OF_BIRTH)
                    )
                )
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
