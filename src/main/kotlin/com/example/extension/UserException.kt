package com.example.extension

import io.ktor.http.HttpStatusCode

class UserException(val httpStatusCode: HttpStatusCode, message: String) : RuntimeException(message) {

}