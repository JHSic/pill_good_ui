package com.example.pill_good.repository

import com.example.pill_good.data.dto.SearchingConditionDTO
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class PillRepositoryTest : RepositoryTest() {
    private val pillRepositoryImpl: PillRepositoryImpl by inject(PillRepositoryImpl::class.java)

    @Test
    fun `pill readById test`() {
        // Create response
        val pillId = 1L

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {
                    "pillIndex": 1,
                    "pillNum": "ABC123",
                    "pillName": "알약 이름",
                    "pillFrontWord": "앞면 문구",
                    "pillBackWord": "뒷면 문구",
                    "pillShape": "원형",
                    "pillColor": "노란색",
                    "pillCategoryName": "카테고리",
                    "pillFormulation": "제형",
                    "pillEffect": "효과",
                    "pillPrecaution": "주의사항"
                }}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {

            val result = pillRepositoryImpl.readById(pillId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.pillIndex == 1L)
            assert(result?.pillNum == "ABC123")
            assert(result?.pillName == "알약 이름")
            assert(result?.pillFrontWord == "앞면 문구")
            assert(result?.pillBackWord == "뒷면 문구")
            assert(result?.pillShape == "원형")
            assert(result?.pillColor == "노란색")
            assert(result?.pillCategoryName == "카테고리")
            assert(result?.pillFormulation == "제형")
            assert(result?.pillEffect == "효과")
            assert(result?.pillPrecaution == "주의사항")
        }
    }

    @Test
    fun `pill readByPillName test`() {
        // Create response
        val pillName = "알약 이름"

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {
                    "pillIndex": 1,
                    "pillNum": "ABC123",
                    "pillName": "알약 이름",
                    "pillFrontWord": "앞면 문구",
                    "pillBackWord": "뒷면 문구",
                    "pillShape": "원형",
                    "pillColor": "노란색",
                    "pillCategoryName": "카테고리",
                    "pillFormulation": "제형",
                    "pillEffect": "효과",
                    "pillPrecaution": "주의사항"
                }}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = pillRepositoryImpl.readByPillName(pillName)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.pillIndex == 1L)
            assert(result?.pillNum == "ABC123")
            assert(result?.pillName == "알약 이름")
            assert(result?.pillFrontWord == "앞면 문구")
            assert(result?.pillBackWord == "뒷면 문구")
            assert(result?.pillShape == "원형")
            assert(result?.pillColor == "노란색")
            assert(result?.pillCategoryName == "카테고리")
            assert(result?.pillFormulation == "제형")
            assert(result?.pillEffect == "효과")
            assert(result?.pillPrecaution == "주의사항")
        }
    }

    @Test
    fun `pill readBySearchingCondition test`() {
        // Searching condition DTO
        val searchingConditionDTO = SearchingConditionDTO(
            pillName = "Pill Name",
            pillShape = "Round",
            pillColor = "Yellow",
            pillFrontWord = "Front Word",
            pillBackWord = "Back Word"
        )

        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": [
                {
                    "pillIndex": 1,
                    "pillNum": "ABC123",
                    "pillName": "Pill Name",
                    "pillFrontWord": "Front Word",
                    "pillBackWord": "Back Word",
                    "pillShape": "Round",
                    "pillColor": "Yellow",
                    "pillCategoryName": "Category",
                    "pillFormulation": "Formulation",
                    "pillEffect": "Effect",
                    "pillPrecaution": "Precaution"
                },
                {
                    "pillIndex": 2,
                    "pillNum": "DEF456",
                    "pillName": "Another Pill",
                    "pillFrontWord": "Other Front",
                    "pillBackWord": "Other Back",
                    "pillShape": "Square",
                    "pillColor": "Blue",
                    "pillCategoryName": "Category",
                    "pillFormulation": "Formulation",
                    "pillEffect": "Effect",
                    "pillPrecaution": "Precaution"
                }
            ]}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Act
        runBlocking {
            // Send Request
            val result = pillRepositoryImpl.readBySearchingCondition(searchingConditionDTO)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.size == 2)

            // Validate first pill
            val firstPill = result?.get(0)
            assert(firstPill?.pillIndex == 1L)
            assert(firstPill?.pillNum == "ABC123")
            assert(firstPill?.pillName == "Pill Name")
            assert(firstPill?.pillFrontWord == "Front Word")
            assert(firstPill?.pillBackWord == "Back Word")
            assert(firstPill?.pillShape == "Round")
            assert(firstPill?.pillColor == "Yellow")
            assert(firstPill?.pillCategoryName == "Category")
            assert(firstPill?.pillFormulation == "Formulation")
            assert(firstPill?.pillEffect == "Effect")
            assert(firstPill?.pillPrecaution == "Precaution")

            // Validate second pill
            val secondPill = result?.get(1)
            assert(secondPill?.pillIndex == 2L)
            assert(secondPill?.pillNum == "DEF456")
            assert(secondPill?.pillName == "Another Pill")
            assert(secondPill?.pillFrontWord == "Other Front")
            assert(secondPill?.pillBackWord == "Other Back")
            assert(secondPill?.pillShape == "Square")
            assert(secondPill?.pillColor == "Blue")
            assert(secondPill?.pillCategoryName == "Category")
            assert(secondPill?.pillFormulation == "Formulation")
            assert(secondPill?.pillEffect == "Effect")
            assert(secondPill?.pillPrecaution == "Precaution")
        }
    }
}