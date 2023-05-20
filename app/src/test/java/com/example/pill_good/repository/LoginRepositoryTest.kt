package com.example.pill_good.repository

import com.example.pill_good.data.dto.LoginDTO
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class LoginRepositoryTest : RepositoryTest() {
    private val loginRepositoryImpl: LoginRepositoryImpl by inject(LoginRepositoryImpl::class.java)

    @Test
    fun `login`() {
        // Test Data
        val loginDTO = LoginDTO(
            userEmail = "test@example.com",
            userToken = "1234567890"
        )

        // Create Response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "statusCode": 200, 
                "message": "Success", 
                "data": { 
                    "userIndex": 1, 
                    "userEmail": "test@example.com",
                    "userFcmToken": "1234567890"
                }
            }
            """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = loginRepositoryImpl.login(loginDTO)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            println(result)

            // Assertion
            assertNotNull(result)
            assertEquals(1L, result?.userIndex)
            assertEquals("test@example.com", result?.userEmail)
            assertEquals("1234567890", result?.userFcmToken)
        }
    }
}