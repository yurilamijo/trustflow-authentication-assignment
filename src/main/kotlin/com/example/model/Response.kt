package com.example.model

import io.ktor.http.HttpStatusCode

data class Response(
    val httpResponseCode: HttpStatusCode,
    val message: String = ""
)