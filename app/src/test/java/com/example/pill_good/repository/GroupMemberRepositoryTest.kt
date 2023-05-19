package com.example.pill_good.repository

import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Test

import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate

class GroupMemberRepositoryTest: RepositoryTest() {
    private val groupMemberRepositoryImpl: GroupMemberRepositoryImpl by inject(GroupMemberRepositoryImpl::class.java)

    @Test
    fun `group-member create test`() {
        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {"groupMemberIndex": 1, "userIndex": 1, "groupMemberName": "John Doe", "groupMemberBirth": "1990-01-01", "groupMemberPhone": "123-456-7890", "messageCheck": true}}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Test Data
        val groupMemberAndUserIndexDTO = GroupMemberAndUserIndexDTO(
            groupMemberName = "John Doe",
            groupMemberBirth = LocalDate.of(1990, 1, 1),
            groupMemberPhone = "123-456-7890",
            messageCheck = true
        )

        // Act
        runBlocking {
            // Send Request
            val result = groupMemberRepositoryImpl.create(groupMemberAndUserIndexDTO)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.groupMemberIndex == 1L)
            assert(result?.userIndex == 1L)
            assert(result?.groupMemberName == "John Doe")
            assert(result?.groupMemberBirth == LocalDate.of(1990, 1, 1))
            assert(result?.groupMemberPhone == "123-456-7890")
            assert(result?.messageCheck == true)
        }
    }

    @Test
    fun `group-member readListByUserId test`() {
        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": [{"groupMemberIndex": 1, "userIndex": 1, "groupMemberName": "John Doe", "groupMemberBirth": "1990-01-01", "groupMemberPhone": "123-456-7890", "messageCheck": true}]}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Test Data
        val userId = 1L

        // Act
        runBlocking {
            // Send Request
            val resultList = groupMemberRepositoryImpl.readListByUserId(userId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(resultList?.isNotEmpty() ?: false)
            val result = resultList?.get(0)
            assert(result?.groupMemberIndex == 1L)
            assert(result?.userIndex == 1L)
            assert(result?.groupMemberName == "John Doe")
            assert(result?.groupMemberBirth == LocalDate.of(1990, 1, 1))
            assert(result?.groupMemberPhone == "123-456-7890")
            assert(result?.messageCheck == true)
        }
    }

    @Test
    fun `group-member readById test`() {
        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {"groupMemberIndex": 1, "userIndex": 1, "groupMemberName": "John Doe", "groupMemberBirth": "1990-01-01", "groupMemberPhone": "123-456-7890", "messageCheck": true}}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Test Data
        val groupMemberId = 1L

        // Act
        runBlocking {
            // Send Request
            val result = groupMemberRepositoryImpl.readById(groupMemberId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.groupMemberIndex == 1L)
            assert(result?.userIndex == 1L)
            assert(result?.groupMemberName == "John Doe")
            assert(result?.groupMemberBirth == LocalDate.of(1990, 1, 1))
            assert(result?.groupMemberPhone == "123-456-7890")
            assert(result?.messageCheck == true)
        }
    }

    @Test
    fun `group-member updateById test`() {
        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {"groupMemberIndex": 1, "userIndex": 1, "groupMemberName": "John Doe", "groupMemberBirth": "1990-01-01", "groupMemberPhone": "123-456-7890", "messageCheck": true}}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Test Data
        val groupMemberAndUserIndexDTO = GroupMemberAndUserIndexDTO(
            groupMemberName = "Updated Name",
            groupMemberBirth = LocalDate.of(1990, 1, 1),
            groupMemberPhone = "123-456-7890",
            messageCheck = true
        )

        // Act
        runBlocking {
            // Send Request
            val result = groupMemberRepositoryImpl.updateById(1L, groupMemberAndUserIndexDTO)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // Assertion
            assert(result != null)
            assert(result?.groupMemberIndex == 1L)
            assert(result?.userIndex == 1L)
            assert(result?.groupMemberName == "John Doe")
            assert(result?.groupMemberBirth == LocalDate.of(1990, 1, 1))
            assert(result?.groupMemberPhone == "123-456-7890")
            assert(result?.messageCheck == true)
        }
    }

    @Test
    fun `group-member deleteById test`() {
        // Create response
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(
                """{"statusCode": 200, "message": "Success", "data": {}}""".trimIndent()
            )

        mockWebServer.enqueue(response)

        // Test Data
        val groupMemberId = 1L

        // Act
        runBlocking {
            // Send Request
            groupMemberRepositoryImpl.deleteById(groupMemberId)

            // Print Request Info
            printMockWebServerInfo(mockWebServer)

            // No assertion required for this test
        }
    }
}
