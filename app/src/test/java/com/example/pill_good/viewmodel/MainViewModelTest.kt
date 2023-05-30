package com.example.pill_good.viewmodel

import com.example.pill_good.data.dto.TakePillAndTakePillCheckAndGroupMemberIndexDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckDTO
import com.example.pill_good.data.model.CalendarData
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.Random

class MainViewModelTest {

    companion object {
        private const val OFFSET = 0L // load하는 Month의 범위
        private const val SIZE_OF_GROUP_MEMBER = 10   // 그룹멤버 사이즈
        private const val SIZE_OF_TAKE_PILL_RANGE = 14    // 최대 복용 기간
    }

    private lateinit var initialCalendarData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO>

    /**
     * TODO Create Mock Data for MainViewModel's Calendar Data
     */
    @Before
    fun createMockData() {
        initialCalendarData = mutableListOf()

        val currentDate = LocalDate.now()
        val startDate = currentDate.minusMonths(OFFSET).withDayOfMonth(1)
        val endDate = currentDate.plusMonths(OFFSET).withDayOfMonth(currentDate.plusMonths(
            OFFSET
        ).lengthOfMonth())

        val random = Random()

        for (i in 1..SIZE_OF_GROUP_MEMBER) {
            val randomDate = LocalDate.ofEpochDay(startDate.toEpochDay() + random.nextInt(endDate.toEpochDay().toInt() - startDate.toEpochDay().toInt()))

            val takePillAndTakePillCheckDTOs = mutableListOf<TakePillAndTakePillCheckDTO>()

            for (j in 1..Random().nextInt(SIZE_OF_TAKE_PILL_RANGE) + 1)  {
                if(LocalDate.now().month + OFFSET < randomDate.plusDays(j.toLong()).month)
                    break

                val takePillAndTakePillCheckDTO = TakePillAndTakePillCheckDTO(
                    takePillIndex = j.toLong(),
                    prescriptionIndex = i.toLong(),
                    pillIndex = i.toLong(),
                    takeDay = i,
                    takeCount = i,
                    takePillCheckIndex = i.toLong(),
                    takeDate = randomDate.plusDays(j.toLong()),
                    takePillTime = i,
                    takeCheck = true
                )

                takePillAndTakePillCheckDTOs.add(takePillAndTakePillCheckDTO)
            }

            val groupMemberData = TakePillAndTakePillCheckAndGroupMemberIndexDTO(
                groupMemberIndex = i.toLong(),
                takePillAndTakePillCheckDTOs = takePillAndTakePillCheckDTOs
            )

            initialCalendarData.add(groupMemberData)
        }
    }

    @Test
    fun `initialData Test`() {
        val calendarDataList: MutableList<CalendarData> = mutableListOf()
        val groupMemberCalendarDataMap: MutableMap<Long, MutableList<CalendarData>> = mutableMapOf()

        // 복용 현황 조회를 위한 매개변수 선언
        val groupMemberIdList = mutableListOf<Long>()

        // initialData를 CalendarData로 가공
        initialCalendarData.forEach {
            val groupMemberId = it.groupMemberIndex
            groupMemberIdList.add(groupMemberId)

            it.takePillAndTakePillCheckDTOs.forEach { takePillAndTakePillCheckDTO ->
                val calendarData = CalendarData(
                    groupMemberId,
                    takePillAndTakePillCheckDTO.takePillIndex,
                    takePillAndTakePillCheckDTO.prescriptionIndex,
                    takePillAndTakePillCheckDTO.pillIndex,
                    takePillAndTakePillCheckDTO.takeDay,
                    takePillAndTakePillCheckDTO.takeCount,
                    takePillAndTakePillCheckDTO.takePillCheckIndex,
                    takePillAndTakePillCheckDTO.takeDate,
                    takePillAndTakePillCheckDTO.takePillTime,
                    takePillAndTakePillCheckDTO.takeCheck
                )

                // 전체 캘린더 리스트 생성
                calendarDataList.add(calendarData)

                // 그룹 멤버 캘린더 생성
                groupMemberCalendarDataMap.getOrPut(groupMemberId) { mutableListOf() }.add(calendarData)
            }
        }

        // 전체 리스트 정렬
        calendarDataList.sortBy { calendarData -> calendarData.takeDate }

        // LiveData: totalCalendar 할당
        val totalCalendarDataMapResult: MutableMap<LocalDate, MutableList<CalendarData>> = mutableMapOf()
        calendarDataList.forEach() {
            totalCalendarDataMapResult.getOrPut(it.takeDate!!) { mutableListOf() }.add(it)
        }

        val groupMemberCalendarDataMapResult: MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>> = mutableMapOf()
        groupMemberCalendarDataMap.forEach() {
            val groupMemberId = it.key
            it.value.forEach() { it1 ->
                groupMemberCalendarDataMapResult.getOrPut(groupMemberId) { mutableMapOf() }.getOrPut(it1.takeDate!!){ mutableListOf() }.add(it1)
            }
        }


        /**
         * Assertion
         */

        // Size가 같은가


        var totalCalendarDataMapResultSize = 0
        totalCalendarDataMapResult.forEach {
            totalCalendarDataMapResultSize += it.value.size
        }

        var groupMemberCalendarDataMapResultSize = 0
        groupMemberCalendarDataMapResult.forEach {
            groupMemberCalendarDataMapResultSize += it.value.size
        }

        assert(totalCalendarDataMapResultSize == groupMemberCalendarDataMapResultSize)


        /*// 오늘 날짜 복용 현황 정보를 위한 최초 데이터 로딩
        val initialTakePillData = takePillRepositoryImpl.readTakePillsByGroupMemberIdListAndTargetDate(groupMemberIdList, LocalDate.now())
        _takePillData.value = mutableMapOf(LocalDate.now() to initialTakePillData as MutableList<MedicationInfoDTO>)*/

        println("end")

    }


}