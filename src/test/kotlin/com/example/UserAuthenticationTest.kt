package com.example

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import com.example.constants.RESPONSE_FIELD_ACCESS_TOKEN
import com.example.constants.TEST_JWT_CONFIG_AUDIENCE
import com.example.constants.TEST_JWT_CONFIG_ISSUER
import com.example.constants.TEST_JWT_CONFIG_REALM
import com.example.constants.TEST_JWT_CONFIG_SECRET
import com.example.model.JWTConfig
import com.example.model.User
import com.example.model.UserLogin
import com.example.plugins.configureDI
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSession
import com.example.repository.FakeTaskRepository
import com.example.repository.FakeUserRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

private const val TEST_VALUE_USER_ID = 1
private const val TEST_VALUE_USERNAME = "YuriLam"
private const val TEST_VALUE_PASSWORD = "PasswordYuri"
private const val TEST_VALUE_FIRSTNAME = "Yuri"
private const val TEST_VALUE_LASTNAME = "Lamijo"
private const val TEST_VALUE_EMAIL = "yuri@test.nl"
private const val TEST_VALUE_DATE_OF_BIRTH = "1999-04-08"

@Serializable
data class UserLoginBody(
    val accessToken: String
)

class UserAuthenticationTest {
    private lateinit var token_jwt: String
    private lateinit var token_session: String

    val fakeTaskRepository = FakeTaskRepository()
    val fakeUserRepository = FakeUserRepository()

    @BeforeTest
    fun startUp() = testApplication {
        JWTConfig.init(TEST_JWT_CONFIG_AUDIENCE, TEST_JWT_CONFIG_ISSUER, TEST_JWT_CONFIG_REALM, TEST_JWT_CONFIG_SECRET)

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

        return@testApplication
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

        val response = client.put("/update/1") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
            setBody(
                User(
                    TEST_VALUE_USER_ID,
                    TEST_VALUE_FIRSTNAME,
                    TEST_VALUE_LASTNAME,
                    TEST_VALUE_EMAIL,
                    LocalDate.parse(TEST_VALUE_DATE_OF_BIRTH)
                )
            )
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
