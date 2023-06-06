package com.example.pill_good.repository

import com.example.pill_good.data.dto.MedicationInfoDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckAndGroupMemberIndexDTO
import com.example.pill_good.data.remote.ApiService
import java.time.LocalDate

class TakePillRepositoryImpl(private val apiService: ApiService) {
    suspend fun readCalendarDataByUserIdBetweenDate(
        userId: Long,
        dateStart: LocalDate,
        dateEnd: LocalDate
    ): List<TakePillAndTakePillCheckAndGroupMemberIndexDTO> {
        val response = apiService.getCalendarDataByUserIdBetweenDate(
            userId,
            dateStart,
            dateEnd
        )
        if (response.statusCode == 200) {
            return response.data
        } else {
            throw Exception()
        }
    }

    suspend fun readTakePillsByGroupMemberIdListAndTargetDate(
        groupMemberIdDTOs: List<Long>,
        targetDate: LocalDate
    ): List<MedicationInfoDTO> {
        val response = apiService.getTakePillsByGroupMemberIdListAndTargetDate(groupMemberIdDTOs, targetDate)
        if (response.statusCode == 200) {
            return response.data
        } else if (response.statusCode == 404) {
            return response.data
        } else {
            throw Exception()
        }
    }

}