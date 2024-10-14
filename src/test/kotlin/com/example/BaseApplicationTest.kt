package com.example

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import com.example.constants.TEST_JWT_CONFIG_AUDIENCE
import com.example.constants.TEST_JWT_CONFIG_ISSUER
import com.example.constants.TEST_JWT_CONFIG_REALM
import com.example.constants.TEST_JWT_CONFIG_SECRET
import com.example.model.JWTConfig
import com.example.model.UserLogin
import com.example.plugins.configureDI
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSession
import com.example.repository.FakeTaskRepository
import com.example.repository.FakeUserRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class BaseApplicationTest {
    protected lateinit var token_jwt: String
    protected lateinit var token_session: String

    protected val fakeTaskRepository = FakeTaskRepository()
    protected val fakeUserRepository = FakeUserRepository()

    protected val TEST_VALUE_USERNAME = "YuriLam"
    protected val TEST_VALUE_PASSWORD = "PasswordYuri"

    fun setupTestApplication(builder: ApplicationTestBuilder) {
        builder.application {
            configureDI()
            configureSerialization()
            configureSession()
            configureSecurity()
            configureRouting(fakeTaskRepository, fakeUserRepository)
        }
    }

    fun createTestClient(builder: ApplicationTestBuilder): HttpClient {
        return builder.createClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    @BeforeTest
    fun startUp() = testApplication {
        JWTConfig.init(TEST_JWT_CONFIG_AUDIENCE, TEST_JWT_CONFIG_ISSUER, TEST_JWT_CONFIG_REALM, TEST_JWT_CONFIG_SECRET)
        setupTestApplication(this)
        val client = createTestClient(this)

        val response: HttpResponse = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLogin(TEST_VALUE_USERNAME, TEST_VALUE_PASSWORD))
        }
        var responseBody = response.body<UserLoginBody>()

        token_jwt = responseBody.accessToken
        token_session = response.headers[HEADER_TRUSTFLOW_SESSION].toString()
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }
}