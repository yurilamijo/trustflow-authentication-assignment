package com.example.constants

// JWT constants
const val JWT_CLAIM_USERNAME = "username"
const val CONFIG_PROPERTY_JWT_AUDIENCE = "jwt.audience"
const val CONFIG_PROPERTY_JWT_ISSUER = "jwt.issuer"
const val CONFIG_PROPERTY_JWT_REALM = "jwt.realm"
const val CONFIG_PROPERTY_JWT_SECRET = "jwt.secret"

// Session constants
const val SESSION_SECRET_KEY = "6819b57a326945c1968f45236589"
const val HEADER_CUSTOM_TRUSTFLOW_SESSION = "trustflow_session"
const val FILE_PATH_SESSION_STORAGE = "build/.sessions"

// Database constants
const val COLUMN_VARCHAR_LENGTH_50 = 50
const val COLUMN_VARCHAR_LENGTH_225 = 255

// Testing global constants
const val RESPONSE_FIELD_ACCESS_TOKEN = "accessToken"
const val HEADER_TRUSTFLOW_SESSION = "trustflow_session"
const val TEST_JWT_CONFIG_AUDIENCE = "jwt-audience"
const val TEST_JWT_CONFIG_ISSUER = "jwt-issuer"
const val TEST_JWT_CONFIG_REALM = "ktor sample app"
const val TEST_JWT_CONFIG_SECRET = "test-secret-123"

