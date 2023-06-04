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
 *
 * @property takePillRepositoryImpl
 * @property groupMemberRepositoryImpl
 *
 *
 * @property userInfo
 *
 * 현재 로그인 된 유저의 정보에 대한 LiveData
 *
 *
 *
 * @property groupMemberCalendar
 *
 * MainActivity의 캘린더 정보에 대한 LiveData
 *
 * 서버에게 받는 데이터는 달력 형식에 맞게 mappingCalendarDataToGroupMemberCalendar()를 사용하여 변환할 필요가 있다.
 *
 *
 *
 * @property loadedDateList
 *
 * MainActivity의 캘린더 정보를 받을 때, 로딩된 month에 대한 정보 리스트
 *
 * 해당 리스트를 통해 이미 로드된 month인지 판별 이후
 *
 * 로드되지 않은 month일 경우 calendar 데이터를 서버에 요청하게 됨
 *
 *
 *
 * @property notLoadedInitialData
 *
 * 테스트를 위한 데이터
 *
 * 테스트 시, 현재 월 +- MONTH_OFFSET 만큼의 초기 MockData를 생성한다.
 *
 * 이후 초기 MockData +- MONTH_OFFSET * MULTIPLIED(오프셋의 몇 배수 만큼 로드할건지)만큼의 테스트 데이터를 미리 생성해둔다.
 *
 * 생성된 데이터가 서버에 있다고 가정한 뒤, onMonthChanged 이벤트 발생에 따른 데이터 로드에 대한 테스트를 진행한다.
 *
 *
 *
 * @property takePillData
 *
 * 화면 하단을 스크롤 업 시 보이는 복용 현황 정보에 대한 LiveData
 *
 * 최초 데이터 로드 시 오늘 날짜에 대한 정보만 가져온다.
 *
 * 특정 날짜를 선택하면 List에 해당 날짜의 복용 현황 정보 유무를 파악 후,
 *
 * 존재하지 않으면 서버와 통신하여 List에 추가하고 View에 띄우게 된다.
 *
 *
 *
 * @property groupMemberList
 *
 * GroupMember에 대한 LiveData
 *
 * 그룹원 조회 화면에 Intent로 넘겨주는 데이터이며,
 *
 * 그룹원에 대한 CRUD는 groupMemberCalendar가 의존하므로 함께 수정되어야 한다.
 *
 *
 *
 */
class MainViewModel(
    private val takePillRepositoryImpl: TakePillRepositoryImpl,
    private val groupMemberRepositoryImpl: GroupMemberRepositoryImpl
) : ViewModel() {

    private val _userInfo = MutableLiveData<UserDTO>()
    val userInfo: LiveData<UserDTO> get() = _userInfo

    private val _groupMemberCalendar =
        MutableLiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>>()
    val groupMemberCalendar: LiveData<MutableMap<Long, MutableMap<LocalDate, MutableList<CalendarData>>>> get() = _groupMemberCalendar

    private val _loadedDateList = MutableLiveData<MutableList<LocalDate>>()
    val loadedDateList: LiveData<MutableList<LocalDate>> get() = _loadedDateList

    private val _takePillData =
        MutableLiveData<MutableMap<LocalDate, MutableMap<String, MutableMap<String, MutableMap<String, MedicationInfoDTO>>>>>()
    val takePillData: LiveData<MutableMap<LocalDate, MutableMap<String, MutableMap<String, MutableMap<String, MedicationInfoDTO>>>>> get() = _takePillData

    private val _groupMemberList = MutableLiveData<MutableList<GroupMemberAndUserIndexDTO>>()
    val groupMemberList: LiveData<MutableList<GroupMemberAndUserIndexDTO>> get() = _groupMemberList

    // for Test
    private var notLoadedInitialData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
        mutableListOf()

    companion object {
        private const val MONTH_OFFSET = 1L

        // For Test
        private const val SIZE_OF_GROUP_MEMBER = 3
        private const val RANGE_OF_TAKE_PILL = 14

        private const val SIZE_OF_DISEASE = 5
        private const val SIZE_OF_PILL = 10

        private const val IS_TEST = true
    }

    /**
     * TODO - Activity에서 전체적인 데이터를 초기화하는 메소드.
     *
     * MainActivity의 onCreate에서 호출되는 메소드. IS_TEST 변수의 값에 따라 초기화 값이 달라진다.
     *
     */
    fun getInitialData() {
        // OFFSET 만큼의 data를 가져옴
        val startDate = LocalDate.now().minusMonths(MONTH_OFFSET).withDayOfMonth(1)
        val endDate = LocalDate.now().plusMonths(MONTH_OFFSET)
            .withDayOfMonth(LocalDate.now().plusMonths(MONTH_OFFSET).lengthOfMonth())

        viewModelScope.launch {
            // 캘린더를 위한 최초 데이터 로딩
            val initialCalendarData: MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> =
                if (IS_TEST) createCalendarMockData(
                    MONTH_OFFSET,
                    SIZE_OF_GROUP_MEMBER,
                    RANGE_OF_TAKE_PILL
                )
                else loadCalendarData(startDate, endDate)

            // For Test: 첫 로드에 포함되지 않는 데이터
            if (IS_TEST) {
                notLoadedInitialData = createNotLoadedMockData(
                    MONTH_OFFSET,
                    SIZE_OF_GROUP_MEMBER,
                    RANGE_OF_TAKE_PILL
                )
                notLoadedInitialData.forEach { data ->
                    data.takePillAndTakePillCheckDTOs.sortedBy { it.takeDate }
                }
            }

            // LiveData: groupMemberCalendar 할당
            _groupMemberCalendar.value =
                mappingCalendarDataToGroupMemberCalendar(initialCalendarData)

            // LiveData: _loadedDateList 할당
            _loadedDateList.value = setLoadedDateList(startDate, endDate)

            // groupMemberIdList 생성
            val groupMemberIdList = groupMemberCalendar.value?.map { it.key } ?: emptyList()

            // LiveData: groupMemberList 할당
            _groupMemberList.value = if (IS_TEST) createMockGroupMemberList(
                userInfo.value?.userIndex!!, groupMemberIdList
            ) as MutableList<GroupMemberAndUserIndexDTO>
            else groupMemberRepositoryImpl.readListByUserId(userInfo.value?.userIndex!!) as MutableList

            // 오늘 날짜 복용 현황 정보를 위한 최초 데이터 로딩
            val initialMedicationInfo: MutableMap<LocalDate, MutableList<MedicationInfoDTO>> =
                if (IS_TEST) createTakePillMockData(SIZE_OF_DISEASE, SIZE_OF_PILL)
                else loadTakePillInfo(LocalDate.now()) ?: mutableMapOf()

            // LiveData: takePillData 할당
            _takePillData.value = mappingMedicationInfoToTakePillInfo(initialMedicationInfo)

            // fot Tset: Log
            if (IS_TEST) getLog()
        }
    }


    /**
     * TODO - Activity에서 해당하는 Month의 캘린더 데이터를 가져오는 메소드
     *
     * @param date 선택된 월 정보
     * @param isAfterMonth monthChange의 방향이 증가인지 감소인지에 대한 판별값
     *
     * MONTH_OFFSET 만큼의 캘린더 데이터를 로드한다.
     */
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

            // loadedDateList 새롭게 갱신
            _loadedDateList.postValue(setLoadedDateList(targetStartDate, targetEndDate))

            // 날짜를 기준으로 정렬 후 LiveData 업데이트
            _groupMemberCalendar.postValue(mergedNewGroupMemberCalendar?.mapValues { entry ->
                entry.value.toSortedMap()
            }?.toSortedMap())
        }
    }


    /**
     * TODO - Activity에서 특정 날짜의 복용 현황을 로드하는 메소드
     *
     * @param targetDate 선택된 날짜
     *
     */
    fun getTakePillInfo(targetDate: Date) {
        // 이미 존재하는 데이터의 경우
        if (_takePillData.value?.containsKey(convertDateToLocalDate(targetDate)) == true)
            return

        // 존재하지 않는 데이터는 로드해온다.
        else {
            if (IS_TEST) {  // 테스트의 경우 이미 모든 데이터를 로드해왔다 가정
                return
            } else {
                val loadTakePill = loadTakePillInfo(convertDateToLocalDate(targetDate))
                _takePillData.value?.putAll(
                    mappingMedicationInfoToTakePillInfo(
                        loadTakePill ?: mutableMapOf()
                    )
                )
            }
        }
    }

    fun setUserInfo(userId: Long, userEmail: String, userFcmToken: String) {
        _userInfo.value = UserDTO(userId, userEmail, userFcmToken)
    }

    fun updateGroupMemberCalendar() {
        _groupMemberCalendar.postValue(_groupMemberCalendar.value)
    }

    /**
     * TODO - LoadedDateList를 설정하는 메소드
     *
     * @param startDate 시작 날짜
     * @param endDate 끝 날짜
     * @return 새로 생성된 loadedDateList
     */
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
            if (!isLoadedDate(tempDate)) newLoadedDateList.add(tempDate)
        }

        newLoadedDateList.sortBy { it }

        return newLoadedDateList
    }


    /**
     * TODO - 이미 로드된 날짜인지 검사하는 메소드
     *
     * @param date 선택 날짜
     * @return 판별값
     */
    private fun isLoadedDate(date: LocalDate): Boolean {
        return loadedDateList.value?.contains(date) ?: false
    }

    /**
     * TODO - 시작 날짜와 끝 날짜 간의 Month의 차이값을 계산하는 메소드
     *
     * @param startDate 시작 날짜
     * @param endDate 끝 날짜
     * @return 시작 날짜와 끝 날짜 간의 차이
     */

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
     * TODO - Converter for LocalDate to Date
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

    /**
     * TODO - Converter for Date to LocalDate
     *
     * @param date
     * @return 변환된 LocalDate
     */
    private fun convertDateToLocalDate(date: Date?): LocalDate {
        return date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            ?: throw IllegalArgumentException("Date cannot be null")
    }

    /**
     * TODO - 특정 기간 내의 캘린더 데이터를 로드하는 메소드
     *
     * @param startDate 시작 날짜
     * @param endDate 끝 날짜
     * @return List<TakePillAndTakePillCheckAndGroupMemberIndexDTO>로 이루어진 캘린더 데이터 (매핑 이전 값)
     */
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

    /**
     * TODO - 특정 날짜의 복용 현황 정보를 가져오는 메소드
     *
     * @param targetDate 선택 날짜
     * @return MutableMap<LocalDate, MutableList<MedicationInfoDTO>>? 선택 날짜와 복용 정보를 매핑한 값
     */
    private fun loadTakePillInfo(targetDate: LocalDate): MutableMap<LocalDate, MutableList<MedicationInfoDTO>>? {
        var targetCalendarData: MutableList<MedicationInfoDTO> =
            emptyList<MedicationInfoDTO>().toMutableList()

        // 이미 가져온 MedicationInfo 인지 확인
        if (_takePillData.value?.containsKey(targetDate) == true)
            return null

        viewModelScope.launch {
            groupMemberList.value?.map { it.groupMemberIndex }?.let { it1 ->
                targetCalendarData =
                    takePillRepositoryImpl.readTakePillsByGroupMemberIdListAndTargetDate(
                        it1 as List<Long>, targetDate
                    ) as MutableList<MedicationInfoDTO>
            }
        }

        return mutableMapOf(targetDate to targetCalendarData)
    }

    /**
     * TODO - 초기 캘린더 데이터를 Live데이터인 groupMemberCalendar의 타입에 맞게 매핑해주는 메소드
     *
     * @param data 초기 캘린더 데이터
     * @return groupMemberCalendar에 할당할 값
     */
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
        groupMemberCalendarDataMap.forEach {
            val groupMemberId = it.key
            it.value.forEach { it1 ->
                result.getOrPut(groupMemberId) { mutableMapOf() }
                    .getOrPut(it1.takeDate!!) { mutableListOf() }.add(it1)
            }
        }

        return result
    }

    /**
     * TODO - 초기 복용 현황 정보를 라이브 데이터인 takePillData의 형식에 맞게 매핑하는 메소드
     *
     * @param medicationInfo 초기 복용 현황 값
     * @return takePillData에 할당할 값
     */
    private fun mappingMedicationInfoToTakePillInfo(medicationInfo: MutableMap<LocalDate, MutableList<MedicationInfoDTO>>): MutableMap<LocalDate, MutableMap<String, MutableMap<String, MutableMap<String, MedicationInfoDTO>>>> {
        val takePillInfo: MutableMap<LocalDate, MutableMap<String, MutableMap<String, MutableMap<String, MedicationInfoDTO>>>> =
            mutableMapOf()

        for ((date, medicationList) in medicationInfo) {
            val dateMap = takePillInfo[date] ?: mutableMapOf()

            for (medication in medicationList) {
                val groupMemberIndex = medication.groupMemberIndex ?: continue
                val groupMemberName = medication.groupMemberName ?: continue
                val diseaseIndex = medication.diseaseIndex ?: continue
                val diseaseName = medication.diseaseName ?: continue
                val pillIndex = medication.pillIndex ?: continue
                val pillName = medication.pillName ?: continue

                val groupMemberMap = dateMap.getOrPut(groupMemberName) { mutableMapOf() }
                val diseaseMap = groupMemberMap.getOrPut(diseaseName) { mutableMapOf() }
                var pillMap = diseaseMap.getOrPut(pillName) { medication }
            }

            takePillInfo[date] = dateMap
        }

        return takePillInfo
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

    /**
     * TODO - 초기 데이터 값을 로그로 남기는 메소드
     */
    private fun getLog() {
        var stringBuilder = StringBuilder()
        var logString: String

        /**
         * TEST PRESET
         */
        Logger.d(
            "Test Preset\n" +
                    "MONTH_OFFSET = $MONTH_OFFSET\n" +
                    "SIZE_OF_GROUP_MEMBER = $SIZE_OF_GROUP_MEMBER\n" +
                    "RANGE_OF_TAKE_PILL = $RANGE_OF_TAKE_PILL\n" +
                    "SIZE_OF_DISEASE = $SIZE_OF_DISEASE" +
                    "SIZE_OF_PILL = $SIZE_OF_PILL"
        )

        /**
         * USET INFO
         */
        Logger.d("User Information: $_userInfo")

        /**
         * GROUP MEMBER LIST
         */
        _groupMemberList.value?.forEach {
            stringBuilder.append("GroupMember Index: ${it.groupMemberIndex}, Name: ${it.groupMemberName}}\n")
        }
        logString = stringBuilder.toString()
        Logger.d("GroupMember Information\n$logString")
        stringBuilder.clear()

        /**
         * GROUP MEMBER CALENDAR
         */
        _groupMemberCalendar.value?.forEach { (groupMemberIndex, dateMap) ->
            dateMap.keys.forEach { date ->
                stringBuilder.append("Group Member Index: $groupMemberIndex, Date: $date\n")
            }
        }
        logString = stringBuilder.toString()
        Logger.d("Value of GroupMemberCalendar\n$logString")
        stringBuilder.clear()

        /**
         * TAKE PILL DATA INFO
         */
        takePillData.value?.forEach { (date, groupMemberMap) ->
            groupMemberMap.forEach { (groupMemberName, diseaseMap) ->
                diseaseMap.forEach { (diseaseName, pillMap) ->
                    pillMap.forEach { (pillName, medicationInfo) ->
                        stringBuilder.append("Date: $date\n")
                        stringBuilder.append("Group Member Name: $groupMemberName\n")
                        stringBuilder.append("Disease Name: $diseaseName\n")
                        stringBuilder.append("Pill Name: $pillName\n")
                        stringBuilder.append("Medication Info: $medicationInfo\n\n")
                    }
                }
            }
        }

        logString = stringBuilder.toString()
        Logger.d("takePillData Information\n$logString")
        stringBuilder.clear()

        /**
         * LOADED DATE LIST
         */
        Logger.d("loadedDateList Information\n${_loadedDateList.value}")

        /**
         * NOT LOADED DATE LIST
         */
        notLoadedInitialData.forEach { data ->
            data.takePillAndTakePillCheckDTOs.forEach { takePillData ->
                stringBuilder.append("Group Member Index: ${data.groupMemberIndex}, ")
                stringBuilder.append("Take Date: ${takePillData.takeDate}\n")
            }
        }

        logString = stringBuilder.toString()
        Logger.d("notLoadedInitialData Information\n$logString")
        stringBuilder.clear()


    }

    /**
     * TODO - 테스트 진행 시 groupMemberCalendar 라이브 데이터의 초기 값을 생성해주는 메소드
     *
     * @param OFFSET 현재 Month를 기준 어느 정도의 OFFSET으로 +-값을 가져올 것인지에 대한 값
     * @param SIZE_OF_GROUP_MEMBER 생성되는 Group member의 수
     * @param RANGE_OF_TAKE_PILL 랜덤으로 생성되는 복용 기간의 최대 범위
     * @return 초기 groupMemberCalendar(매핑 이전)의 MockData
     */
    private fun createCalendarMockData(
        OFFSET: Long, SIZE_OF_GROUP_MEMBER: Int, RANGE_OF_TAKE_PILL: Int
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

            for (j in 1..Random().nextInt(RANGE_OF_TAKE_PILL) + 1) {
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

    /**
     * TODO - 테스트 진행 시 takePillData 라이브 데이터의 초기 값을 생성해주는 메소드
     *
     * @param SIZE_OF_DISEASE 랜덤으로 생성되는 질병의 최대 수
     * @param SIZE_OF_PILL 랜덤으로 생성되는 약의 최대 수
     * @return 초기 takePillData의(매핑 이전) MockData
     */
    private fun createTakePillMockData(
        SIZE_OF_DISEASE: Int, SIZE_OF_PILL: Int
    ): MutableMap<LocalDate, MutableList<MedicationInfoDTO>> {
        val takePillData =
            mutableMapOf<LocalDate, MutableList<MedicationInfoDTO>>()

        var map = mutableMapOf<Long, MutableList<LocalDate>>()

        val random = Random()

        var groupMemberIndexList = mutableListOf<Long>()

        _groupMemberCalendar.value?.forEach {
            it.value.forEach { it1 ->
                map.getOrPut(it.key) { mutableListOf() }.add(it1.key)
                groupMemberIndexList.add(it.key)
            }
        }

        var dateList = map.flatMap { it.value }

        for (i in dateList.indices) {
            var medicationInfoDTO: MedicationInfoDTO
            val medicationInfoList = mutableListOf<MedicationInfoDTO>()

            val targetGroupMember =
                _groupMemberList.value?.find { it.groupMemberIndex == groupMemberIndexList[i] }
            val targetTakePillData =
                _groupMemberCalendar.value?.get(targetGroupMember?.groupMemberIndex)

            val groupMemberName = targetGroupMember?.groupMemberName

            val diseaseCount = random.nextInt(SIZE_OF_DISEASE) + 1

            for (j in 0 until diseaseCount) {
                val diseaseName = "Disease ${j + 1}"

                val pillCount = random.nextInt(SIZE_OF_PILL) + 1

                val takeCheck = random.nextBoolean()

                for (k in 0 until pillCount) {
                    val takePillTime = random.nextInt(5) + 1

                    medicationInfoDTO = MedicationInfoDTO(
                        groupMemberIndex = groupMemberIndexList[i],
                        groupMemberName = groupMemberName,
                        pillIndex = k.toLong() + 1,
                        pillName = "Pill ${k + 1}",
                        diseaseIndex = j.toLong() + 1,
                        diseaseName = diseaseName,
                        takePillCheckIndex = "${j + 1}${k + 1}".toLong(),
                        takeCheck = takeCheck,
                        takePillTime = takePillTime
                    )
                    medicationInfoList.add(medicationInfoDTO)
                }
            }
            takePillData[dateList[i]]?.addAll(medicationInfoList) ?: medicationInfoList
            takePillData.getOrPut(dateList[i]) { mutableListOf() }.addAll(medicationInfoList)
        }

        return takePillData
    }

    /**
     * TODO - 테스트 진행시 생성되는 아직 로드되지 않은 groupMemberCalandar 데이터를 생성해주는 메소드
     *
     * @param OFFSET 현재 Month를 기준 어느 정도의 OFFSET으로 +-값을 가져올 것인지에 대한 값
     * @param SIZE_OF_GROUP_MEMBER 생성되는 Group member의 수
     * @param RANGE_OF_TAKE_PILL 랜덤으로 생성되는 복용 기간의 최대 범위
     * @param MULTIPLIED OFFSET을 기준으로 MockData의 생성 범위 증가 배수 (최소 1)
     * @return 아직 로드되지 않은 초기 groupMemberCalendar(매핑 이전)의 MockData
     */
    private fun createNotLoadedMockData(
        OFFSET: Long, SIZE_OF_GROUP_MEMBER: Int, RANGE_OF_TAKE_PILL: Int, MULTIPLIED: Int = 2
    ): MutableList<TakePillAndTakePillCheckAndGroupMemberIndexDTO> {
        val initialCalendarData = mutableListOf<TakePillAndTakePillCheckAndGroupMemberIndexDTO>()

        val currentDate = LocalDate.now()
        val startDate = currentDate.minusMonths(OFFSET).withDayOfMonth(1)
        val endDate = currentDate.plusMonths(OFFSET).withDayOfMonth(
            currentDate.plusMonths(
                OFFSET
            ).lengthOfMonth()
        )
        val monthToSubtractAndAdd =
            if (OFFSET == 0L) MULTIPLIED
            else OFFSET * MULTIPLIED


        val notLoadedStartDate =
            currentDate.minusMonths(monthToSubtractAndAdd.toLong()).withDayOfMonth(1)
        val notLoadedEndDate =
            currentDate.plusMonths(monthToSubtractAndAdd.toLong()).withDayOfMonth(
                currentDate.plusMonths(
                    OFFSET
                ).lengthOfMonth()
            )

        val random = Random()

        if (MULTIPLIED != 0) {
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

                    for (j in 1..Random().nextInt(RANGE_OF_TAKE_PILL) + 1) {
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

    /**
     * TODO - 테스트 진행시 생성되는 그룹원의 랜덤 정보를 생성하는 메소드
     *
     * @param userId 유저의 id
     * @param groupMemberIdList 그룹 멤버의 id 리스트
     * @return 그룹 멤버 리스트의 MockData
     */
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

    /**
     * TODO - 테스트 진행시 랜덤으로 생성되는 그룹 멤버의 핸드폰 번호를 랜덤으로 생성하는 메소드
     *
     * @return 랜덤 생성된 전화번호
     */
    private fun generateRandomPhoneNumber(): String {
        val randomDigits = (1_000..9_999).random() // 4자리 임의의 숫자 생성
        return "010-$randomDigits-${(1_000..9_999).random()}" // 예시: "010-1234-7890"
    }
}