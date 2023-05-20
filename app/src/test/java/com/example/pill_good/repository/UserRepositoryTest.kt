package com.example.pill_good.repository

import com.example.pill_good.data.dto.UserDTO
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class UserRepositoryTest : RepositoryTest() {
    private val userRepositoryImpl: UserRepositoryImpl by inject(UserRepositoryImpl::class.java)

    @Test
    fun `user updateUserToken test`() {
        // Test Data
        val userId = 1L
        val userDTO = UserDTO(userFcmToken = "newToken")

        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {
                "userIndex": 1,
                "userEmail": "user@example.com",
                "userFcmToken": "newToken"
            }}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = userRepositoryImpl.updateUserToken(userId, userDTO)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assertNotNull(result)
            assertEquals(1L, result?.userIndex)
            assertEquals("user@example.com", result?.userEmail)
            assertEquals("newToken", result?.userFcmToken)
        }
    }

    @Test
    fun `user deleteUser test`() {
        // Test Data
        val userId = 1L

        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success"}"""
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = userRepositoryImpl.deleteUser(userId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // No assertion required for this test
        }
    }
}