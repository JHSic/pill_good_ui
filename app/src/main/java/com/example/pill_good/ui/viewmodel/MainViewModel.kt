package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.MedicationInfoDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckAndGroupMemberIndexDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckDTO
import com.example.pill_good.data.model.CalendarData
import com.example.pill_good.repository.GroupMemberRepositoryImpl
import com.example.pill_good.repository.TakePillRepositoryImpl
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Random

class MainViewModel(
    private val takePillRepositoryImpl: TakePillRepositoryImpl,
    private val groupMemberRepositoryImpl: GroupMemberRepositoryImpl
) : ViewModel() {

    private val _totalCalendar = MutableLiveData<MutableMap<LocalDate, MutableList<CalendarData>>>()
    val totalCalendar: LiveData<MutableMap<LocalDate, MutableList<CalendarData>>> get() = _totalCalendar

    private val _groupMemberCalendar =
        MutableLiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>>()
    val groupMemberCalendar: LiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>> get() = _groupMemberCalendar

    private val _takePillData =
        MutableLiveData<MutableMap<LocalDate, MutableList<MedicationInfoDTO>>>()
    val takePillData: LiveData<MutableMap<LocalDate, MutableList<MedicationInfoDTO>>> get() = _takePillData

    private val _groupMemberList = MutableLiveData<MutableList<GroupMemberAndUserIndexDTO>>()
    val groupMemberList: LiveData<MutableList<GroupMemberAndUserIndexDTO>> get() = _groupMemberList

    companion object {
        private const val MONTH_OFFSET = 1L
    }

    fun loadInitialCalendarData(isTest: Boolean = false) {

        // 전체 캘린더 및 그룹 멤버 캘린더 리스트 선언
        val calendarDataList: MutableList<CalendarData> = mutableListOf()
        val groupMemberCalendarDataMap: MutableMap<Long, MutableList<CalendarData>> = mutableMapOf()

        // 오늘 날짜를 기준 OFFSET 만큼의 InitialData를 가져옴
        val userId = 1L // 임시 데이터. 로그인 성공시 가져 와야 함
        val startDate = LocalDate.now().minusMonths(MONTH_OFFSET).withDayOfMonth(1)
        val endDate = LocalDate.now().plusMonths(MONTH_OFFSET)
            .withDayOfMonth(LocalDate.now().plusMonths(MONTH_OFFSET).lengthOfMonth())

        viewModelScope.launch {
            // 캘린더를 위한 최초 데이터 로딩
            val initialCalendarData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
                if (isTest)
                    createMockData(1L, 10, 14)
                else
                    takePillRepositoryImpl.readCalendarDataByUserIdBetweenDate(
                        userId,
                        startDate,
                        endDate
                    ) as MutableList


            // 그룹 멤버를 위한 Id List 선언
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

                    // 그룹 멤버 캘린더 맵 생성
                    groupMemberCalendarDataMap.getOrPut(groupMemberId) { mutableListOf() }
                        .add(calendarData)
                }
            }

            // 전체 리스트 정렬
            calendarDataList.sortBy { calendarData -> calendarData.takeDate }

            // LiveData: totalCalendar 할당
            val totalCalendarDataMapResult: MutableMap<LocalDate, MutableList<CalendarData>> =
                mutableMapOf()
            calendarDataList.forEach() {
                totalCalendarDataMapResult.getOrPut(it.takeDate!!) { mutableListOf() }.add(it)
            }

            _totalCalendar.value = totalCalendarDataMapResult

            // LiveData: groupMemberCalendar 할당
            val groupMemberCalendarDataMapResult: MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>> =
                mutableMapOf()
            groupMemberCalendarDataMap.forEach() {
                val groupMemberId = it.key
                it.value.forEach() { it1 ->
                    groupMemberCalendarDataMapResult.getOrPut(groupMemberId) { mutableMapOf() }
                        .getOrPut(it1.takeDate!!) { mutableListOf() }.add(it1)
                }
            }

            _groupMemberCalendar.value = groupMemberCalendarDataMapResult


            // 오늘 날짜 복용 현황 정보를 위한 최초 데이터 로딩
            val initialTakePillData: MutableList<MedicationInfoDTO> = if (isTest)
                mutableListOf()
            else
                takePillRepositoryImpl.readTakePillsByGroupMemberIdListAndTargetDate(
                    groupMemberIdList,
                    LocalDate.now()
                ) as MutableList<MedicationInfoDTO>

            // LiveData: takePillData 할당
            _takePillData.value =
                mutableMapOf(LocalDate.now() to initialTakePillData)

            // LiveData: groupMemberList 할당
            _groupMemberList.value = if(isTest)
                createMockGroupMemberList(userId, groupMemberIdList) as MutableList<GroupMemberAndUserIndexDTO>
            else
                groupMemberRepositoryImpl.readListByUserId(userId) as MutableList<GroupMemberAndUserIndexDTO>
        }
    }

    /**
     * ====================== Below is for the TEST case ======================
     */

    private fun createMockData(
        OFFSET: Long,
        SIZE_OF_GROUP_MEMBER: Int,
        SIZE_OF_TAKE_PILL_RANGE: Int
    ): MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> {
        val initialCalendarData = mutableListOf<TakePillAndTakePillCheckAndGroupMemberIndexDTO>()

        val currentDate = LocalDate.now()
        val startDate = currentDate.minusMonths(OFFSET).withDayOfMonth(1)
        val endDate = currentDate.plusMonths(OFFSET).withDayOfMonth(
            currentDate.plusMonths(
                OFFSET
            ).lengthOfMonth()
        )

        val random = Random()

        for (i in 1..SIZE_OF_GROUP_MEMBER) {
            val randomDate = LocalDate.ofEpochDay(
                startDate.toEpochDay() + random.nextInt(
                    endDate.toEpochDay().toInt() - startDate.toEpochDay().toInt()
                )
            )

            val takePillAndTakePillCheckDTOs = mutableListOf<TakePillAndTakePillCheckDTO>()

            for (j in 1..Random().nextInt(SIZE_OF_TAKE_PILL_RANGE) + 1) {
                if (LocalDate.now().month + OFFSET < randomDate.plusDays(j.toLong()).month)
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

        return initialCalendarData
    }

    private fun createMockGroupMemberList(userId: Long, groupMemberIdList: List<Long>): List<GroupMemberAndUserIndexDTO> {
        val groupMemberList = mutableListOf<GroupMemberAndUserIndexDTO>()

        for (groupMemberId in groupMemberIdList) {
            val randomBirthYear = kotlin.random.Random.nextInt(1970, 2000) // 1970부터 2000 사이의 임의의 연도
            val randomBirthMonth = kotlin.random.Random.nextInt(10, 13) // 1부터 12 사이의 임의의 월
            val randomBirthDay = kotlin.random.Random.nextInt(10, 29) // 1부터 28 사이의 임의의 일자

            val groupMemberData = GroupMemberAndUserIndexDTO(
                groupMemberIndex = groupMemberId,
                userIndex = userId, // 사용자 인덱스를 설정하세요.
                groupMemberName = "Group Member $groupMemberId", // 그룹 멤버 이름을 설정하세요.
                groupMemberBirth = LocalDate.of(randomBirthYear, randomBirthMonth, randomBirthDay), // 그룹 멤버 생년월일을 설정하세요.
                groupMemberPhone = generateRandomPhoneNumber(), // 그룹 멤버 전화번호를 설정하세요. (랜덤 함수 호출)
                messageCheck = kotlin.random.Random.nextBoolean() // 메시지 체크 여부를 설정하세요. (랜덤 함수 호출)
            )

            groupMemberList.add(groupMemberData)
        }

        return groupMemberList
    }

    private fun generateRandomPhoneNumber(): String {
        val randomDigits = (1_000..9_999).random() // 4자리 임의의 숫자 생성
        return "010-$randomDigits-${(1_000..9_999).random()}" // 예시: "010-123456-7890"
    }
}