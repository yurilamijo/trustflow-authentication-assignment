package com.example

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import com.example.constants.RESPONSE_FIELD_ACCESS_TOKEN
import com.example.enum.UserRole
import com.example.model.User
import com.example.model.UserLogin
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.server.testing.testApplication
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

private const val TEST_VALUE_USER_ID = 1
private const val TEST_VALUE_FIRSTNAME = "Yuri"
private const val TEST_VALUE_LASTNAME = "Lamijo"
private const val TEST_VALUE_EMAIL = "yuri@test.nl"
private const val TEST_VALUE_DATE_OF_BIRTH = "1999-04-08"

@Serializable
data class UserLoginBody(
    val accessToken: String
)

class UserAuthenticationTest : BaseApplicationTest() {
    @Test
    fun `Login should succeed`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

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
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.put("/user/update/{id}")
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `API call with token should succeed`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.put("/user/update/1") {
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
                    LocalDate.parse(TEST_VALUE_DATE_OF_BIRTH),
                    UserRole.ADMIN
                )
            )
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
