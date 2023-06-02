package com.example.pill_good.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.MedicationInfoDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckAndGroupMemberIndexDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckDTO
import com.example.pill_good.data.dto.UserDTO
import com.example.pill_good.data.model.CalendarData
import com.example.pill_good.repository.GroupMemberRepositoryImpl
import com.example.pill_good.repository.TakePillRepositoryImpl
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Random

/**
 * TODO
 *
 * @property takePillRepositoryImpl
 * @property groupMemberRepositoryImpl
 *
 * @property userInfo
 * 현재 로그인 된 유저의 정보에 대한 LiveData
 *
 * @property groupMemberCalendar
 * MainActivity의 캘린더 정보에 대한 LiveData
 * 서버에게 받는 데이터는 달력 형식에 맞게 mappingCalendarDataToGroupMemberCalendar()를 사용하여 변환할 필요가 있다.
 *
 * @property loadedMonthList
 *
 * @property takePillData
 * 화면 하단을 스크롤 업 시 보이는 복용 현황 정보에 대한 LiveData
 * 최초 데이터 로드 시 오늘 날짜에 대한 정보만 가져온다.
 * 특정 날짜를 선택하면 List에 해당 날짜의 복용 현황 정보 유무를 파악 후,
 * 존재하지 않으면 서버와 통신하여 List에 추가하고 View에 띄우게 된다.
 *
 * @property groupMemberList
 * GroupMember에 대한 LiveData
 * 그룹원 조회 화면에 Intent로 넘겨주는 데이터이며,
 * 그룹원에 대한 CRUD는 groupMemberCalendar가 의존하므로 함께 수정되어야 한다.
 */
class MainViewModel(
    private val takePillRepositoryImpl: TakePillRepositoryImpl,
    private val groupMemberRepositoryImpl: GroupMemberRepositoryImpl
) : ViewModel() {

    private val _userInfo = MutableLiveData<UserDTO>()
    private val userInfo: LiveData<UserDTO> get() = _userInfo

    private val _groupMemberCalendar =
        MutableLiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>>()
    val groupMemberCalendar: LiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>> get() = _groupMemberCalendar

    private val _loadedDateList = MutableLiveData<MutableList<LocalDate>>()
    val loadedDateList: LiveData<MutableList<LocalDate>> get() = _loadedDateList

    private val _takePillData =
        MutableLiveData<MutableMap<LocalDate, MutableList<MedicationInfoDTO>>>()
    val takePillData: LiveData<MutableMap<LocalDate, MutableList<MedicationInfoDTO>>> get() = _takePillData

    private val _groupMemberList = MutableLiveData<MutableList<GroupMemberAndUserIndexDTO>>()
    val groupMemberList: LiveData<MutableList<GroupMemberAndUserIndexDTO>> get() = _groupMemberList

    // for Test
    private var notLoadedInitialData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
        mutableListOf()

    companion object {
        private const val MONTH_OFFSET = 1L

        // For Test
        private const val SIZE_OF_GROUP_MEMBER = 2
        private const val SIZE_OF_TAKE_PILL_RANGE = 5
        const val IS_TEST = true
    }

    fun getInitialData() {
        // OFFSET 만큼의 data를 가져옴
        val startDate = LocalDate.now().minusMonths(MONTH_OFFSET).withDayOfMonth(1)
        val endDate = LocalDate.now().plusMonths(MONTH_OFFSET)
            .withDayOfMonth(LocalDate.now().plusMonths(MONTH_OFFSET).lengthOfMonth())

        viewModelScope.launch {
            // 캘린더를 위한 최초 데이터 로딩
            val initialCalendarData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
                if (IS_TEST) createMockData(
                    MONTH_OFFSET,
                    SIZE_OF_GROUP_MEMBER,
                    SIZE_OF_TAKE_PILL_RANGE
                )
                else loadCalendarData(startDate, endDate)

            // For Test: 첫 로드에 포함되지 않는 데이터
            if (IS_TEST) {
                notLoadedInitialData = createNotLoadedMockData(
                    MONTH_OFFSET,
                    SIZE_OF_GROUP_MEMBER,
                    SIZE_OF_TAKE_PILL_RANGE
                )
                notLoadedInitialData.forEach { data ->
                    data.takePillAndTakePillCheckDTOs.sortedBy { it.takeDate }
                }
            }

            // LiveData: groupMemberCalendar 할당
            _groupMemberCalendar.value =
                mappingCalendarDataToGroupMemberCalendar(initialCalendarData)

            // LiveData: loadedMonthList 할당
            _loadedDateList.value = setLoadedDateList(startDate, endDate)

            // groupMemberIdList 생성
            val groupMemberIdList = groupMemberCalendar.value?.map { it.key } ?: emptyList()

            // 오늘 날짜 복용 현황 정보를 위한 최초 데이터 로딩
            val initialTakePillData: MutableMap<LocalDate, MutableList<MedicationInfoDTO>> =
                if (IS_TEST) mutableMapOf()
                else loadTakePillInfo(LocalDate.now())

            // LiveData: takePillData 할당
            _takePillData.value = initialTakePillData

            // LiveData: groupMemberList 할당
            _groupMemberList.value = if (IS_TEST) createMockGroupMemberList(
                userInfo.value?.userIndex!!, groupMemberIdList
            ) as MutableList<GroupMemberAndUserIndexDTO>
            else groupMemberRepositoryImpl.readListByUserId(userInfo.value?.userIndex!!) as MutableList

            // fot Tset: Log
            if (IS_TEST) getLog()
        }
    }

    fun getCalendarDataFromSelectedMonth(date: Date, isAfterMonth: Boolean) {
        if (_loadedDateList.value?.size ?: 0 <= 0)
            return

        // OFFSET 만큼의 data를 가져옴
        val targetDate = convertDateToLocalDate(date)
        targetDate.withDayOfMonth(1)
        val targetStartDate = targetDate.minusMonths(MONTH_OFFSET)
        val targetEndDate = targetDate.plusMonths(MONTH_OFFSET)
        var calendarData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO>

        var gap: Int =
            if (isAfterMonth) getMonthGap(loadedDateList.value?.last()!!, targetEndDate)
            else getMonthGap(targetStartDate, loadedDateList.value?.first()!!)

        if (gap <= 0)
            return

        viewModelScope.launch {
            if (IS_TEST) {
                calendarData =
                    if (isAfterMonth) {
                        notLoadedInitialData
                            .filter {
                                it.takePillAndTakePillCheckDTOs.any {
                                    it.takeDate in targetDate..targetDate.plusMonths(
                                        MONTH_OFFSET
                                    ).withDayOfMonth(
                                        targetDate.plusMonths(MONTH_OFFSET).lengthOfMonth()
                                    )
                                }
                            }
                            .toMutableList()
                    } else {
                        notLoadedInitialData
                            .filter {
                                it.takePillAndTakePillCheckDTOs.any {
                                    it.takeDate in targetDate.minusMonths(
                                        MONTH_OFFSET
                                    )..targetDate.withDayOfMonth(targetDate.lengthOfMonth())
                                }
                            }
                            .toMutableList()
                    }
            } else {
                calendarData =
                        // 현재 달 보다 이후의 달을 조회할 경우
                    if (isAfterMonth)
                        loadCalendarData(
                            targetDate,
                            targetDate.plusMonths(MONTH_OFFSET)
                                .withDayOfMonth(targetDate.plusMonths(MONTH_OFFSET).lengthOfMonth())
                        )

                    // 현재 달 보다 이전의 달을 조회할 경우
                    else
                        loadCalendarData(
                            targetDate.minusMonths(MONTH_OFFSET),
                            targetDate.withDayOfMonth(targetDate.lengthOfMonth())
                        )
            }

            val mergedNewGroupMemberCalendar = _groupMemberCalendar.value?.toMutableMap()
            mappingCalendarDataToGroupMemberCalendar(calendarData).forEach { (key, value) ->
                mergedNewGroupMemberCalendar?.set(
                    key,
                    (mergedNewGroupMemberCalendar[key]?.plus(value)
                        ?: value) as MutableMap<LocalDate, MutableList<CalendarData>>
                )
            }

            // 날짜를 기준으로 정렬 후 LiveData 업데이트
            _groupMemberCalendar.postValue(mergedNewGroupMemberCalendar?.mapValues { entry ->
                entry.value.toSortedMap()
            }?.toSortedMap())
        }
    }

    fun getTakePillInfo(targetDate: LocalDate) {
        // TODO("Not Yet Implemented")
    }

    fun setUserInfo(userId: Long, userEmail: String, userFcmToken: String) {
        _userInfo.value = UserDTO(userId, userEmail, userFcmToken)
    }

    fun updateGroupMemberCalendar() {
        _groupMemberCalendar.postValue(_groupMemberCalendar.value)
    }

    private fun setLoadedDateList(
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): MutableList<LocalDate> {
        if (groupMemberCalendar.value == null)
            return mutableListOf()

        val newLoadedDateList = loadedDateList.value?.toMutableList() ?: mutableListOf()

        val gap = getMonthGap(startDate, endDate)

        for (i in 0..gap) {
            val tempDate = startDate.plusMonths(i.toLong()).withDayOfMonth(1)
            if (!isLoadedDate(tempDate)) newLoadedDateList?.add(tempDate)

        }

        newLoadedDateList?.sortBy { it }

        return newLoadedDateList
    }

    private fun isLoadedDate(date: LocalDate): Boolean {
        return loadedDateList.value?.contains(date) ?: false
    }

    private fun getMonthGap(startDate: LocalDate, endDate: LocalDate?): Int {
        val startYear = startDate.year
        val startMonth = startDate.monthValue

        val endYear = endDate?.year ?: LocalDate.now().year
        val endMonth = endDate?.monthValue ?: LocalDate.now().monthValue

        val yearDiff = endYear - startYear
        val monthDiff = endMonth - startMonth

        return yearDiff * 12 + monthDiff
    }

    /**
     * TODO Converter for LocalDate to Date
     * CalendarDay.from의 인자에 값을 대입하기 위해 사용
     *
     * @param localDate
     * @return 변환된 Date
     */
    private fun convertLocalDateToDate(localDate: LocalDate?): Date {
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = localDate?.atStartOfDay(zoneId)
        return Date.from(zonedDateTime?.toInstant())
    }

    private fun convertDateToLocalDate(date: Date?): LocalDate {
        return date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            ?: throw IllegalArgumentException("Date cannot be null")
    }

    private fun loadCalendarData(
        startDate: LocalDate,
        endDate: LocalDate? = null
    ): MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> {
        var result: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
            emptyList<TakePillAndTakePillCheckAndGroupMemberIndexDTO>().toMutableList()

        viewModelScope.launch {
            result = if (endDate != null)
                takePillRepositoryImpl.readCalendarDataByUserIdBetweenDate(
                    userInfo.value?.userIndex!!,
                    startDate,
                    endDate
                ) as MutableList
            else
                takePillRepositoryImpl.readCalendarDataByUserIdBetweenDate(
                    userInfo.value?.userIndex!!,
                    startDate,
                    startDate.withDayOfMonth(startDate.lengthOfMonth())
                ) as MutableList

        }

        return result
    }

    private fun loadTakePillInfo(targetDate: LocalDate): MutableMap<LocalDate, MutableList<MedicationInfoDTO>> {
        var targetCalendarData: MutableList<MedicationInfoDTO> =
            emptyList<MedicationInfoDTO>().toMutableList()

        viewModelScope.launch {
            groupMemberList.value?.map { it.groupMemberIndex }?.let { it1 ->
                targetCalendarData =
                    takePillRepositoryImpl.readTakePillsByGroupMemberIdListAndTargetDate(
                        it1 as List<Long>, targetDate
                    ) as MutableList<MedicationInfoDTO>
            }
        }

        return mutableMapOf(LocalDate.now() to targetCalendarData)
    }

    private fun mappingCalendarDataToGroupMemberCalendar(data: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO>): MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>> {
        // 전체 캘린더 및 그룹 멤버 캘린더 리스트 선언
        val calendarDataList: MutableList<CalendarData> = mutableListOf()
        val groupMemberCalendarDataMap: MutableMap<Long, MutableList<CalendarData>> = mutableMapOf()

        // data를 CalendarData로 가공
        data.forEach {
            val groupMemberId = it.groupMemberIndex

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

        // LiveData: groupMemberCalendar 할당
        val result: MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>> =
            mutableMapOf()
        groupMemberCalendarDataMap.forEach() {
            val groupMemberId = it.key
            it.value.forEach() { it1 ->
                result.getOrPut(groupMemberId) { mutableMapOf() }
                    .getOrPut(it1.takeDate!!) { mutableListOf() }.add(it1)
            }
        }

        return result
    }

    /*fun testAddCalendarData() {
        val calendarDataList = mutableListOf<CalendarData>()

        val currentDate = LocalDate.now().plusMonths(2).withDayOfMonth(15)

        // 새로운 CalendarData 객체를 생성하고 필요한 데이터를 설정합니다
        val newCalendarData = CalendarData(
            groupMemberId = 1L,
            takePillIndex = 2L,
            prescriptionIndex = 3L,
            pillIndex = 4L,
            takeDay = 5,
            takeCount = 2,
            takePillCheckIndex = 6L,
            takeDate = currentDate,
            takePillTime = 10,
            takeCheck = true
        )

        if (_totalCalendar.value == null) {
            _totalCalendar.value = mutableMapOf()
        }

        // _totalCalendar의 값을 가져와서 업데이트합니다
        val totalCalendarValue = _totalCalendar.value ?: mutableMapOf()

        if (totalCalendarValue.containsKey(currentDate)) {
            // 이미 해당 날짜에 데이터가 있는 경우에는 기존 리스트에 추가합니다
            val dataList = totalCalendarValue[currentDate]
            dataList?.add(newCalendarData)
        } else {
            // 해당 날짜에 데이터가 없는 경우에는 새로운 리스트를 생성하여 데이터를 추가합니다
            val dataList = mutableListOf(newCalendarData)
            totalCalendarValue[currentDate] = dataList
        }

        // _totalCalendar 값을 업데이트합니다
        _totalCalendar.value = totalCalendarValue
    }*/

    /**
     * ====================== Below is for the TEST case ======================
     */

    private fun getLog() {
        var stringBuilder = StringBuilder()
        var logString: String

        Logger.d("User Information: $_userInfo")

        _groupMemberCalendar.value?.forEach { (groupMemberIndex, dateMap) ->
            dateMap.keys.forEach { date ->
                stringBuilder.append("Group Member Index: $groupMemberIndex, Date: $date\n")
            }
        }
        logString = stringBuilder.toString()
        Logger.d("Value of GroupMemberCalendar\n$logString")
        stringBuilder.clear()

        Logger.d("Value of takePillData\n${_takePillData.value}")

        _groupMemberList.value?.forEach {
            stringBuilder.append("GroupMember Index: ${it.groupMemberIndex}, Name: ${it.groupMemberName}}\n")
        }
        logString = stringBuilder.toString()
        Logger.d("GroupMember Information\n$logString")
        stringBuilder.clear()

        Logger.d("loadedDateList Information\n${_loadedDateList.value}")

        notLoadedInitialData.forEach { data ->
            data.takePillAndTakePillCheckDTOs.forEach { takePillData ->
                stringBuilder.append("Group Member Index: ${data.groupMemberIndex}, ")
                stringBuilder.append("Take Date: ${takePillData.takeDate}\n")
            }
        }

        logString = stringBuilder.toString()
        Logger.d("notLoadedInitialData Information\n$logString")
    }

    private fun createMockData(
        OFFSET: Long, SIZE_OF_GROUP_MEMBER: Int, SIZE_OF_TAKE_PILL_RANGE: Int
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
                val isOver = endDate.month < randomDate.plusDays(j.toLong()).month
                if (isOver) break

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

    private fun createNotLoadedMockData(
        OFFSET: Long, SIZE_OF_GROUP_MEMBER: Int, SIZE_OF_TAKE_PILL_RANGE: Int, MULTIPLIED: Int = 2
    ): MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> {
        val initialCalendarData = mutableListOf<TakePillAndTakePillCheckAndGroupMemberIndexDTO>()

        val currentDate = LocalDate.now()
        val startDate = currentDate.minusMonths(OFFSET).withDayOfMonth(1)
        val endDate = currentDate.plusMonths(OFFSET).withDayOfMonth(
            currentDate.plusMonths(
                OFFSET
            ).lengthOfMonth()
        )
        val notLoadedStartDate = currentDate.minusMonths(OFFSET * MULTIPLIED).withDayOfMonth(1)
        val notLoadedEndDate = currentDate.plusMonths(OFFSET * MULTIPLIED).withDayOfMonth(
            currentDate.plusMonths(
                OFFSET
            ).lengthOfMonth()
        )

        val random = Random()

        if (MULTIPLIED != 1) {
            for (i in 1..MULTIPLIED) {
                for (i in 1..SIZE_OF_GROUP_MEMBER) {
                    var randomDate: LocalDate

                    do {
                        randomDate = LocalDate.ofEpochDay(
                            notLoadedStartDate.toEpochDay() + random.nextInt(
                                notLoadedEndDate.toEpochDay()
                                    .toInt() - notLoadedStartDate.toEpochDay()
                                    .toInt()
                            )
                        )
                    } while (startDate.month.value <= randomDate.month.value && randomDate.month.value <= endDate.month.value)
                    val takePillAndTakePillCheckDTOs = mutableListOf<TakePillAndTakePillCheckDTO>()

                    for (j in 1..Random().nextInt(SIZE_OF_TAKE_PILL_RANGE) + 1) {
                        val isOver =
                            notLoadedEndDate.month < randomDate.plusDays(j.toLong()).month
                        val isInside =
                            startDate.month <= randomDate.plusDays(j.toLong()).month &&
                            randomDate.plusDays(j.toLong()).month <= endDate.month

                        if (isOver || isInside) break

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
        }

        return initialCalendarData
    }

    private fun createMockGroupMemberList(
        userId: Long, groupMemberIdList: List<Long>
    ): List<GroupMemberAndUserIndexDTO> {
        val groupMemberList = mutableListOf<GroupMemberAndUserIndexDTO>()

        for (groupMemberId in groupMemberIdList) {
            val randomBirthYear = kotlin.random.Random.nextInt(1970, 2000) // 1970부터 2000 사이의 임의의 연도
            val randomBirthMonth = kotlin.random.Random.nextInt(1, 13) // 1부터 12 사이의 임의의 월
            val randomBirthDay = kotlin.random.Random.nextInt(1, 29) // 1부터 28 사이의 임의의 일자

            val groupMemberData = GroupMemberAndUserIndexDTO(
                groupMemberIndex = groupMemberId, userIndex = userId, // 사용자 인덱스를 설정하세요.
                groupMemberName = "GMember $groupMemberId", // 그룹 멤버 이름을 설정하세요.
                groupMemberBirth = LocalDate.of(
                    randomBirthYear, randomBirthMonth, randomBirthDay
                ), // 그룹 멤버 생년월일을 설정하세요.
                groupMemberPhone = generateRandomPhoneNumber(), // 그룹 멤버 전화번호를 설정하세요. (랜덤 함수 호출)
                messageCheck = kotlin.random.Random.nextBoolean() // 메시지 체크 여부를 설정하세요. (랜덤 함수 호출)
            )

            groupMemberList.add(groupMemberData)
        }

        return groupMemberList
    }

    private fun generateRandomPhoneNumber(): String {
        val randomDigits = (1_000..9_999).random() // 4자리 임의의 숫자 생성
        return "010-$randomDigits-${(1_000..9_999).random()}" // 예시: "010-1234-7890"
    }
}