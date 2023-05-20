package com.example.pill_good.repository

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime

class NotificationRepositoryTest: RepositoryTest() {
    private val notificationRepositoryImpl: NotificationRepositoryImpl by inject(NotificationRepositoryImpl::class.java)

    @Test
    fun `notification readByUserId test`() {
        // Create response
        val userId = 1L
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": [
                {"notificationIndex": 1, "notificationContent": "Notification 1", "notificationTime": "2023-05-19T10:00:00", "notificationCheck": true},
                {"notificationIndex": 2, "notificationContent": "Notification 2", "notificationTime": "2023-05-19T11:00:00", "notificationCheck": false}
            ]}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = notificationRepositoryImpl.readByUserId(userId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.size == 2)

            val notification1 = result?.get(0)
            assert(notification1?.notificationIndex == 1L)
            assert(notification1?.notificationContent == "Notification 1")
            assert(notification1?.notificationTime == LocalDateTime.of(2023, 5, 19, 10, 0, 0))
            assert(notification1?.notificationCheck == true)

            val notification2 = result?.get(1)
            assert(notification2?.notificationIndex == 2L)
            assert(notification2?.notificationContent == "Notification 2")
            assert(notification2?.notificationTime == LocalDateTime.of(2023, 5, 19, 11, 0, 0))
            assert(notification2?.notificationCheck == false)
        }
    }

}
