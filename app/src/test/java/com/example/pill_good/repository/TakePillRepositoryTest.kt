package com.example.pill_good.repository

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate

class TakePillRepositoryTest : RepositoryTest() {
    private val takePillRepositoryImpl: TakePillRepositoryImpl by inject(TakePillRepositoryImpl::class.java)

    @Test
    fun `takePill readCalendarDataByUserIdBetweenDate test`() {
        // Test Data
        val userId = 1L

        // Create response
        val dateStart = LocalDate.parse("2023-05-01")
        val dateEnd = LocalDate.parse("2023-05-31")
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "statusCode": 200,
                "message": "Success",
                "data": [
                    {
                        "groupMemberIndex": 1,
                        "takePillAndTakePillCheckDTOs": [
                            {
                                "takePillIndex": 1,
                                "prescriptionIndex": 1,
                                "pillIndex": 1,
                                "takeDay": 1,
                                "takeCount": 1,
                                "takePillCheckIndex": 1,
                                "takeDate": "2023-05-05",
                                "takePillTime": 10,
                                "takeCheck": true
                            },
                            {
                                "takePillIndex": 2,
                                "prescriptionIndex": 2,
                                "pillIndex": 2,
                                "takeDay": 2,
                                "takeCount": 2,
                                "takePillCheckIndex": 2,
                                "takeDate": "2023-05-10",
                                "takePillTime": 15,
                                "takeCheck": false
                            }
                        ]
                    },
                    {
                        "groupMemberIndex": 2,
                        "takePillAndTakePillCheckDTOs": [
                            {
                                "takePillIndex": 3,
                                "prescriptionIndex": 3,
                                "pillIndex": 3,
                                "takeDay": 1,
                                "takeCount": 1,
                                "takePillCheckIndex": 3,
                                "takeDate": "2023-05-15",
                                "takePillTime": 8,
                                "takeCheck": true
                            }
                        ]
                    }
                ]
            }
            """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = takePillRepositoryImpl.readCalendarDataByUserIdBetweenDate(
                userId,
                dateStart,
                dateEnd
            )

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result.size == 2)

            // First group member
            val groupMember1 = result[0]
            assert(groupMember1.groupMemberIndex == 1L)
            assert(groupMember1.takePillAndTakePillCheckDTOs.size == 2)
            val takePill1 = groupMember1.takePillAndTakePillCheckDTOs[0]
            assert(takePill1.takePillIndex == 1L)
            assert(takePill1.prescriptionIndex == 1L)
            assert(takePill1.pillIndex == 1L)
            assert(takePill1.takeDay == 1)
            assert(takePill1.takeCount == 1)
            assert(takePill1.takePillCheckIndex == 1L)
            assert(takePill1.takeDate == LocalDate.parse("2023-05-05"))
            assert(takePill1.takePillTime == 10)
            assert(takePill1.takeCheck == true)
            val takePill2 = groupMember1.takePillAndTakePillCheckDTOs[1]
            assert(takePill2.takePillIndex == 2L)
            assert(takePill2.prescriptionIndex == 2L)
            assert(takePill2.pillIndex == 2L)
            assert(takePill2.takeDay == 2)
            assert(takePill2.takeCount == 2)
            assert(takePill2.takePillCheckIndex == 2L)
            assert(takePill2.takeDate == LocalDate.parse("2023-05-10"))
            assert(takePill2.takePillTime == 15)
            assert(takePill2.takeCheck == false)

            // Second group member
            val groupMember2 = result[1]
            assert(groupMember2.groupMemberIndex == 2L)
            assert(groupMember2.takePillAndTakePillCheckDTOs.size == 1)
            val takePill3 = groupMember2.takePillAndTakePillCheckDTOs[0]
            assert(takePill3.takePillIndex == 3L)
            assert(takePill3.prescriptionIndex == 3L)
            assert(takePill3.pillIndex == 3L)
            assert(takePill3.takeDay == 1)
            assert(takePill3.takeCount == 1)
            assert(takePill3.takePillCheckIndex == 3L)
            assert(takePill3.takeDate == LocalDate.parse("2023-05-15"))
            assert(takePill3.takePillTime == 8)
            assert(takePill3.takeCheck == true)
        }
    }

    @Test
    fun `takePill readTakePillsByGroupMemberIdListAndTargetDate test`() {
        // Test Data
        val groupMemberIndexList = listOf(1L, 2L)
        val targetDate = LocalDate.parse("2023-05-19")

        // Create response
        val medicationInfo1Json = """
    {
        "groupMemberIndex": 1,
        "groupMemberName": "Group Member 1",
        "pillIndex": 1,
        "pillName": "Pill 1",
        "diseaseIndex": 1,
        "diseaseName": "Disease 1",
        "takePillCheckIndex": 1,
        "takeCheck": true,
        "takePillTime": 10
    }""".trimIndent()

        val medicationInfo2Json = """
    {
        "groupMemberIndex": 2,
        "groupMemberName": "Group Member 2",
        "pillIndex": 2,
        "pillName": "Pill 2",
        "diseaseIndex": 2,
        "diseaseName": "Disease 2",
        "takePillCheckIndex": 2,
        "takeCheck": false,
        "takePillTime": 15
    }""".trimIndent()

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
        {
            "statusCode": 200,
            "message": "Success",
            "data": [
                $medicationInfo1Json,
                $medicationInfo2Json
            ]
        }
        """.trimIndent()
            )

        mockWebServer.enqueue(response)

        // Call the method
        runBlocking {
            val result = takePillRepositoryImpl.readTakePillsByGroupMemberIdListAndTargetDate(
                groupMemberIndexList,
                targetDate
            )

            printMockWebServerInfo(mockWebServer)

            // Assertions
            assert(result != null)
            assert(result?.size == 2)

            // Check the first medication info
            val medicationInfo1Result = result?.get(0)
            assert(medicationInfo1Result != null)
            assert(medicationInfo1Result?.groupMemberIndex == 1L)
            assert(medicationInfo1Result?.groupMemberName == "Group Member 1")
            assert(medicationInfo1Result?.pillIndex == 1L)
            assert(medicationInfo1Result?.pillName == "Pill 1")
            assert(medicationInfo1Result?.diseaseIndex == 1L)
            assert(medicationInfo1Result?.diseaseName == "Disease 1")
            assert(medicationInfo1Result?.takePillCheckIndex == 1L)
            assert(medicationInfo1Result?.takeCheck == true)
            assert(medicationInfo1Result?.takePillTime == 10)

            // Check the second medication info
            val medicationInfo2Result = result?.get(1)
            assert(medicationInfo2Result != null)
            assert(medicationInfo2Result?.groupMemberIndex == 2L)
            assert(medicationInfo2Result?.groupMemberName == "Group Member 2")
            assert(medicationInfo2Result?.pillIndex == 2L)
            assert(medicationInfo2Result?.pillName == "Pill 2")
            assert(medicationInfo2Result?.diseaseIndex == 2L)
            assert(medicationInfo2Result?.diseaseName == "Disease 2")
            assert(medicationInfo2Result?.takePillCheckIndex == 2L)
            assert(medicationInfo2Result?.takeCheck == false)
            assert(medicationInfo2Result?.takePillTime == 15)
        }
    }
}