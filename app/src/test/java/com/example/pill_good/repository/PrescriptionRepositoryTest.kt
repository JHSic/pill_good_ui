package com.example.pill_good.repository

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.time.LocalDate

class PrescriptionRepositoryTest : RepositoryTest() {
    private val prescriptionRepositoryImpl: PrescriptionRepositoryImpl by inject(
        PrescriptionRepositoryImpl::class.java
    )

    @Test
    fun `prescription createByImage test`() {
        // Test Data
        val userId = 1L
        val groupMemberId = 1L
        val imgPath = "./src/main/assets/image/aimyon.jpg"
        val prescriptionImage = File(imgPath)

        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {
                "prescriptionIndex": 1,
                "groupMemberIndex": 1,
                "diseaseIndex": 1,
                "prescriptionRegistrationDate": "2023-05-19",
                "prescriptionDate": "2023-05-18",
                "hospitalPhone": "123-456-7890",
                "hospitalName": "Test Hospital"
            }}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result =
                prescriptionRepositoryImpl.createByImage(userId, groupMemberId, prescriptionImage)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.prescriptionIndex == 1L)
            assert(result?.groupMemberIndex == 1L)
            assert(result?.diseaseIndex == 1L)
            assert(result?.prescriptionRegistrationDate == LocalDate.of(2023, 5, 19))
            assert(result?.prescriptionDate == LocalDate.of(2023, 5, 18))
            assert(result?.hospitalPhone == "123-456-7890")
            assert(result?.hospitalName == "Test Hospital")
        }
    }

    @Test
    fun `prescription readListByGroupMemberId test`() {
        // Create response
        val groupMemberId = 1L
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "statusCode": 200,
                "message": "Success",
                "data": [
                    {
                        "prescriptionIndex": 1,
                        "groupMemberIndex": 1,
                        "diseaseIndex": 1,
                        "prescriptionRegistrationDate": "2023-05-19",
                        "prescriptionDate": "2023-05-18",
                        "hospitalPhone": "123-456-7890",
                        "hospitalName": "Test Hospital",
                        "diseaseName": "Test Disease"
                    },
                    {
                        "prescriptionIndex": 2,
                        "groupMemberIndex": 1,
                        "diseaseIndex": 2,
                        "prescriptionRegistrationDate": "2023-05-19",
                        "prescriptionDate": "2023-05-18",
                        "hospitalPhone": "123-456-7890",
                        "hospitalName": "Test Hospital",
                        "diseaseName": "Test Disease"
                    }
                ]
            }
            """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = prescriptionRepositoryImpl.readListByGroupMemberId(groupMemberId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result!!.size == 2)

            val prescription1 = result[0]
            assert(prescription1.prescriptionIndex == 1L)
            assert(prescription1.groupMemberIndex == 1L)
            assert(prescription1.diseaseIndex == 1L)
            assert(prescription1.prescriptionRegistrationDate == LocalDate.of(2023, 5, 19))
            assert(prescription1.prescriptionDate == LocalDate.of(2023, 5, 18))
            assert(prescription1.hospitalPhone == "123-456-7890")
            assert(prescription1.hospitalName == "Test Hospital")
            assert(prescription1.diseaseName == "Test Disease")

            val prescription2 = result[1]
            assert(prescription2.prescriptionIndex == 2L)
            assert(prescription2.groupMemberIndex == 1L)
            assert(prescription2.diseaseIndex == 2L)
            assert(prescription2.prescriptionRegistrationDate == LocalDate.of(2023, 5, 19))
            assert(prescription2.prescriptionDate == LocalDate.of(2023, 5, 18))
            assert(prescription2.hospitalPhone == "123-456-7890")
            assert(prescription2.hospitalName == "Test Hospital")
            assert(prescription2.diseaseName == "Test Disease")
        }
    }

    @Test
    fun `prescription deleteById test`() {
        // Create response
        val id = 1L
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "statusCode": 200,
                "message": "Success"
            }
            """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = prescriptionRepositoryImpl.deleteById(id)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // No assertion required for this test
        }
    }


}