package com.example

import com.example.constants.HEADER_TRUSTFLOW_SESSION
import com.example.constants.RESPONSE_FIELD_ACCESS_TOKEN
import com.example.enum.UserRole
import com.example.model.User
import com.example.model.UserLogin
import com.example.model.UserRegister
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
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

private const val TEST_VALUE_USER_ID_WITH_USER_ROLE_ADMIN = 1
private const val TEST_VALUE_USER_ID_WITH_USER_ROLE_USER = 2
private const val TEST_VALUE_FIRSTNAME = "Yuri"
private const val TEST_VALUE_LASTNAME = "Lamijo"
private const val TEST_VALUE_EMAIL = "yuri@test.nl"
private const val TEST_VALUE_DATE_OF_BIRTH = "1999-04-08"
private const val TEST_VALUE_USERNAME_WITH_USER_ROLE_USER = "RobbertJan"

@Serializable
data class UserLoginBody(
    val accessToken: String
)

class UserAuthenticationTest : BaseApplicationTest() {
    @Test
    fun `Test user login`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(UserLogin(TEST_VALUE_USERNAME, TEST_VALUE_PASSWORD))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), RESPONSE_FIELD_ACCESS_TOKEN)
    }

    @Test
    fun `Test user logout`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.get("/logout") {
            headers {
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val taskResponse = client.get("/tasks") {
            headers {
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }
        assertEquals(HttpStatusCode.Unauthorized, taskResponse.status)
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
                    TEST_VALUE_USER_ID_WITH_USER_ROLE_ADMIN,
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

    @Test
    fun `Test user register`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val userRegister = UserRegister("Miquel", "Lamijo", "MiquelLamijo", "PasswordMiquel")
        val response = client.post("/register") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            setBody(userRegister)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    fun `Test user update own account while having UserRole USER`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        userLogin(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER, TEST_VALUE_PASSWORD)

        val user = User(
            firstName = "Kees",
            lastName = "Jan",
            email = "kees@hotmail.com",
            dateOfBirth = LocalDate.parse("1980-08-04")
        )
        val userAuth = fakeUserRepository.getUserAuthByUsername(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER)
        val response = client.put("/user/update/${userAuth?.userId}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
            setBody(user)
            contentType(ContentType.Application.Json)
        }
        val responseBody = response.body<User>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Kees", responseBody.firstName)
        assertEquals("Jan", responseBody.lastName)
        assertEquals("kees@hotmail.com", responseBody.email)
    }

    @Test
    fun `Test user update other account while having UserRole USER fails`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        userLogin(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER, TEST_VALUE_PASSWORD)

        val user = User(
            firstName = "Kees",
            lastName = "Jan",
            email = "kees@hotmail.com",
            dateOfBirth = LocalDate.parse("1980-08-04")
        )
        val UserAdmin = fakeUserRepository.getUserById(TEST_VALUE_USER_ID_WITH_USER_ROLE_ADMIN)
        val response = client.put("/user/update/${UserAdmin.id}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
            setBody(user)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `Test admin user update other account while having UserRole ADMIN`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val user = User(
            firstName = "Kees",
            lastName = "Jan",
            email = "kees@hotmail.com",
            dateOfBirth = LocalDate.parse("1980-08-04")
        )
        val userAuth = fakeUserRepository.getUserAuthByUsername(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER)
        val response = client.put("/user/update/${userAuth?.userId}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
            setBody(user)
            contentType(ContentType.Application.Json)
        }
        val responseBody = response.body<User>()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Kees", responseBody.firstName)
        assertEquals("Jan", responseBody.lastName)
        assertEquals("kees@hotmail.com", responseBody.email)
    }

    @Test
    fun `Test user deletes own account while having UserRole USER`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        userLogin(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER, TEST_VALUE_PASSWORD)

        val userAuth = fakeUserRepository.getUserAuthByUsername(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER)
        val response = client.delete("/user/delete/${userAuth?.userId}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `Test user deletes other account while having UserRole USER`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        userLogin(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER, TEST_VALUE_PASSWORD)

        val userAdmin = fakeUserRepository.getUserById(TEST_VALUE_USER_ID_WITH_USER_ROLE_ADMIN)
        val response = client.delete("/user/delete/${userAdmin.id}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `Test admin user deletes other account while having UserRole ADMIN`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val userAuth = fakeUserRepository.getUserAuthByUsername(TEST_VALUE_USERNAME_WITH_USER_ROLE_USER)
        val response = client.delete("/user/delete/${userAuth?.userId}") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `Test admin user get all users with UserRole USER`() = testApplication {
        setupTestApplication(this)
        val client = createTestClient(this)

        val response = client.get("/user/byRole/USER") {
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token_jwt")
                header(HEADER_TRUSTFLOW_SESSION, token_session)
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
