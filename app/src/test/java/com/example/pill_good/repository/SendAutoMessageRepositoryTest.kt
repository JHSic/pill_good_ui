package com.example.pill_good.repository

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime

class SendAutoMessageRepositoryTest : RepositoryTest() {
    private val sendAutoMessageRepositoryImpl: SendAutoMessageRepositoryImpl by inject(
        SendAutoMessageRepositoryImpl::class.java
    )

    @Test
    fun `sendAutoMessage readByUserId test`() {
        // Test Data
        val userId = 1L

        // Create Response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "statusCode": 200,
                "message": "Success",
                "data": [
                    {
                        "diseaseName": "Flu",
                        "takeDate": "2023-05-19T10:00:00",
                        "takePillTime": "2023-05-19T10:15:00",
                        "messageContent": "Please take your medication."
                    },
                    {
                        "diseaseName": "Headache",
                        "takeDate": "2023-05-20T08:00:00",
                        "takePillTime": "2023-05-20T08:30:00",
                        "messageContent": "Don't forget to take your headache medicine."
                    }
                ]
            }
            """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = sendAutoMessageRepositoryImpl.readByUserId(userId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.size == 2)
            assert(result?.get(0)?.diseaseName == "Flu")
            assert(result?.get(0)?.takeDate == LocalDateTime.parse("2023-05-19T10:00:00"))
            assert(result?.get(0)?.takePillTime == LocalDateTime.parse("2023-05-19T10:15:00"))
            assert(result?.get(0)?.messageContent == "Please take your medication.")
            assert(result?.get(1)?.diseaseName == "Headache")
            assert(result?.get(1)?.takeDate == LocalDateTime.parse("2023-05-20T08:00:00"))
            assert(result?.get(1)?.takePillTime == LocalDateTime.parse("2023-05-20T08:30:00"))
            assert(result?.get(1)?.messageContent == "Don't forget to take your headache medicine.")
        }
    }
}