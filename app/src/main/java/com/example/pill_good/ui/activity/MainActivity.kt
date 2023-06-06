package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pill_good.R
import com.example.pill_good.data.dto.MedicationInfoDTO
import com.example.pill_good.data.dto.PillDTO
import com.example.pill_good.data.model.SpinnerData
import com.example.pill_good.ui.decorator.DotDecorator
import com.example.pill_good.ui.decorator.NotDayOfMonthDecorator
import com.example.pill_good.ui.decorator.SaturdayDecorator
import com.example.pill_good.ui.decorator.SundayDecorator
import com.example.pill_good.ui.decorator.TodayDecorator
import com.example.pill_good.ui.viewmodel.MainViewModel
import com.google.firebase.storage.FirebaseStorage
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.properties.Delegates

class MainActivity : CustomActionBarActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    private var currentYear = LocalDate.now().year
    private var currentMonth = LocalDate.now().month.value - 1
    private var buttonId by Delegates.notNull<Int>()
    private lateinit var calendar: MaterialCalendarView
    private lateinit var decorators: ArrayList<DayViewDecorator>

    private var selectedGroupMember: Long? = null   // 그룹원 캘린더에서 선택된 그룹 멤버의 Id 정보
    private var selectedSpinnerIndex: Int? = null
    private var selectedDate = LocalDate.now()
    private var selectedTimeBtn: Int = 0
    private var isTotalCalendarMode: Boolean = true

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addCustomView(R.layout.activity_main)
        val inflater = LayoutInflater.from(this)

        // Initializing userInfo
        mainViewModel.setUserInfo(
            intent.getLongExtra("userId", -1),
            intent.getStringExtra("userEmail")!!,
            intent.getStringExtra("userFcmToken")!!
        )


        // Load InitialData
        mainViewModel.getInitialData()

        // LiveData 구독
        mainViewModel.groupMemberCalendar.observe(this) {
            val calendarRadioGroup: RadioGroup = findViewById(R.id.calendar_radio_group)

            if (buttonId == calendarRadioGroup.getChildAt(0).id) {
                setDecorators(currentMonth, isReplaceDecorator = true)
            } else {
                setDecorators(
                    currentMonth,
                    isReplaceDecorator = true,
                    isGroupMemberDecorator = true
                )
            }
        }

        mainViewModel.groupMemberList.observe(this) {
            // TODO - 그룹 멤버 리스트를 다른 액티비티에 넘겨준 후 다시 돌아왔을 때 변경점 감지에 대한 구현 필요
        }

        mainViewModel.takePillData.observe(this) {
            // TODO - 그룹 멤버 리스트를 다른 액티비티에 넘겨준 후 다시 돌아왔을 때 변경점 감지에 대한 구현 필요 (그룹원 삭제나 별칭 수정 등)
        }


        // 캘린더 버튼, 버튼 미지정 설정 및 캘린더 버튼 alpha 변경
        val calendarButton: ImageButton = findViewById(R.id.calendar_button)
        val logoText: TextView = findViewById(R.id.logo)
        calendarButton.alpha = 1f
        calendarButton.isEnabled = false
        logoText.isEnabled = false

        calendar = findViewById(R.id.calendar)
        calendar.setSelectedDate(CalendarDay.today())

        // 현재 선택된 캘린더 ID 설정
        val calendarRadioGroup: RadioGroup = findViewById(R.id.calendar_radio_group)
        buttonId = calendarRadioGroup.checkedRadioButtonId

        // 데코레이터 설정
        decorators = arrayListOf()
        setDecorators(LocalDate.now().monthValue - 1)

        // 슬라이딩 패널 버튼 초기화
        setSelectedTimeBtnByNowTime()

        // 슬라이딩 패널 레이아웃 설정
        val slidingUpPanelLayout: SlidingUpPanelLayout = findViewById(R.id.main_frame)
        setSlidingUpPanelLayout(inflater, selectedTimeBtn, LocalDate.now())

        // 슬라이딩 패널 복용 시간 버튼 리스너 설정
        val takeTimeBtn: RadioGroup = findViewById(R.id.time_btn_group)
        takeTimeBtn.setOnCheckedChangeListener { group, btnId ->
            val radioBtn = group.findViewById<RadioButton>(btnId)
            selectedTimeBtn = group.indexOfChild(radioBtn)
            setSlidingUpPanelLayout(inflater, selectedTimeBtn, selectedDate)
        }

        // onDateChangedListener 설정
        calendar.setOnDateChangedListener(object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                selectedDate = convertDateToLocalDate(date.date)
                mainViewModel.getTakePillInfo(date.date)
                setSelectedTimeBtnByNowTime()
                setSlidingUpPanelLayout(inflater, selectedTimeBtn, selectedDate)
                slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }
        })

        // onMonthChangedListener 설정
        calendar.setOnMonthChangedListener { _, date ->
            val currentYear = this.currentYear
            val currentMonth = this.currentMonth
            val selectedYear = date.year
            val selectedMonth = date.month

            if (selectedYear > currentYear || (selectedYear == currentYear && selectedMonth > currentMonth)) {
                // 다음 달로 넘어갔을 때의 처리
                this.currentMonth = selectedMonth
                mainViewModel.getCalendarDataFromSelectedMonth(date.date, isAfterMonth = true)
            } else if (selectedYear < currentYear || (selectedYear == currentYear && selectedMonth < currentMonth)) {
                // 이전 달로 넘어갔을 때의 처리
                this.currentMonth = selectedMonth
                mainViewModel.getCalendarDataFromSelectedMonth(date.date, isAfterMonth = false)
            } else {
                // 현재 월일 때의 처리
            }

            val calendarRadioGroup: RadioGroup = findViewById(R.id.calendar_radio_group)
            if (buttonId == calendarRadioGroup.getChildAt(0).id) {
                setDecorators(this.currentMonth, isReplaceDecorator = true)
            } else {
                setDecorators(
                    this.currentMonth,
                    isReplaceDecorator = true,
                    isGroupMemberDecorator = true
                )
            }
        }

        // 라디오 그룹에 대한 설정
        initializeCalendarRadioGroup()
    }

    // 캘린더 선택 라디오 버튼 그룹 생성
    fun initializeCalendarRadioGroup() {
        val calendarRadioGroup: RadioGroup = findViewById(R.id.calendar_radio_group)
        calendarRadioGroup.setOnCheckedChangeListener { _, btnId ->
            buttonId = btnId
            when (buttonId) {
                // 전체 캘린더 선택 시 로직
                R.id.calendar_all -> {
                    isTotalCalendarMode = true
                    allCalendarSelect()
                }
                /* 그룹원 캘린더 선택 시 로직
                   스피너를 캘린더 이전에 추가
                */
                R.id.calendar_group_member -> {
                    isTotalCalendarMode = false
                    groupMemberCalenderSelect()
                }
            }
        }
    }

    // 전체 캘린더 선택 시 로직
    private fun allCalendarSelect() {
        val groupMemberSelectorFrame: FrameLayout =
            findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews()

        // 데코레이터 설정
        setDecorators(currentMonth, isReplaceDecorator = true)
    }

    private fun groupMemberCalenderSelect() {
        val groupMemberSelectorFrame: FrameLayout =
            findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews()

        val inflater = LayoutInflater.from(this)

        // Spinner 설정
        val groupMemberSelectorContent: FrameLayout =
            inflater.inflate(R.layout.activity_group_member_selector, null) as FrameLayout
        val groupMemberSelectorParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        groupMemberSelectorContent.layoutParams = groupMemberSelectorParams
        groupMemberSelectorFrame.addView(groupMemberSelectorContent)

        // Spinner adapter 설정
        val groupMemberSpinner = findViewById<Spinner>(R.id.group_member_spinner)

        val spinnerData = ArrayList<SpinnerData>()
        mainViewModel.groupMemberList.value?.forEach {
            spinnerData.add(SpinnerData(it.groupMemberIndex, it.groupMemberName))
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerData
        )
        groupMemberSpinner.adapter = adapter

        // 초기 selectedGroupMember 설정
        if (selectedGroupMember == null && mainViewModel.groupMemberList.value != null) {
            selectedGroupMember = mainViewModel.groupMemberList.value!!.first().groupMemberIndex
            selectedSpinnerIndex = 0

        }

        // 이전에 저장한 인덱스의 그룹 멤버 캘린더 조회
        groupMemberSpinner.setSelection(selectedSpinnerIndex!!)

        // Spinner onClickListener 설정
        groupMemberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 아이템이 선택되었을 때의 동작을 구현
                val selectedItem = parent?.getItemAtPosition(position) as SpinnerData
                selectedGroupMember = selectedItem.id
                selectedSpinnerIndex = position

                // 선택된 아이템에 대한 처리를 수행합니다.
                if (selectedGroupMember != null)
                    setDecorators(
                        currentMonth,
                        isReplaceDecorator = true,
                        isGroupMemberDecorator = true
                    )
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // 데코레이터 설정
        setDecorators(currentMonth, isReplaceDecorator = true, isGroupMemberDecorator = true)
    }

    /**
     * TODO - 캘린더의 데코레이터를 설정해주는 메소드
     * Boolean값을 이용해 Decorator의 상태 및 전체 or 그룹원 캘린더에 대한 데코레이터를 설정할 수 있게끔 구현하였음
     *
     * @param curMonth Decorator를 적용하는 기준이 되는 month. View에 표현되는 해당 month의 day와 그 외 day를 구분하기 위해 사용
     * @param isReplaceDecorator 기존 Decorator를 대체하는지에 대한 여부를 받음
     * @param isGroupMemberDecorator 전체 캘린더인지 그룹원 캘린더인지에 대한 구분을 위해 사용
     */
    private fun setDecorators(
        month: Int,
        isReplaceDecorator: Boolean = false,
        isGroupMemberDecorator: Boolean = false
    ) {
        if (isReplaceDecorator) {
            val newDecorators = arrayListOf<DayViewDecorator>()

            // 기존 데코레이터 중 NotDayOfMonthDecorator를 제외한 데코레이터들을 새로운 리스트에 추가
            decorators.forEach { decorator ->
                if (isTotalCalendarMode) {
                    if (decorator !is NotDayOfMonthDecorator)
                        newDecorators.add(decorator)
                } else {
                    if (decorator !is NotDayOfMonthDecorator && decorator !is DotDecorator)
                        newDecorators.add(decorator)
                }
            }

            // 현재 월에 해당하는 NotDayOfMonthDecorator를 새로운 리스트에 추가
            newDecorators.add(NotDayOfMonthDecorator(currentMonth))

            // 복약일정이 있는 날짜에 데코레이팅
            // 그룹 멤버 데코레이터라면 전부 데코레이팅, 아니라면 선택된 그룹멤버만 데코레이팅
            mainViewModel.groupMemberCalendar.value?.forEach {
                if (!isGroupMemberDecorator || selectedGroupMember != null && it.key == selectedGroupMember) {   // NullCheck 필요없을수도
                    it.value.forEach { it1 ->
                        newDecorators.add(DotDecorator(CalendarDay.from(convertLocalDateToDate(it1.key))))
                    }
                }
            }

            // 새로운 데코레이터 리스트를 적용
            calendar.removeDecorators()
            calendar.addDecorators(newDecorators)
            decorators = newDecorators

        } else {
            decorators.add(SundayDecorator())
            decorators.add(SaturdayDecorator())
            decorators.add(NotDayOfMonthDecorator(month))
            decorators.add(TodayDecorator())

            // 복약일정이 있는 날짜에 데코레이팅
            mainViewModel.groupMemberCalendar.value?.forEach {
                it.value.forEach { it1 ->
                    decorators.add(DotDecorator(CalendarDay.from(convertLocalDateToDate(it1.key))))
                }
            }

            calendar.addDecorators(decorators)
        }
    }

    private fun setSlidingUpPanelLayout(
        inflater: LayoutInflater,
        btnAt: Int,
        targetDate: LocalDate
    ) {
        // 복약 시간 형식에 따라 라디오 그룹에 대한 함수화 예정
        val slideLayout: LinearLayout = findViewById(R.id.slide_layout)
        var time = 0

        // TODO - time과 takeTime 매칭시키기
        time = btnAt + 1

        val diseaseContainer: LinearLayout = findViewById(R.id.group_member_disease_container)
        val diseaseContainerScrollView: ScrollView =
            findViewById(R.id.group_member_disease_container_scroll)
        diseaseContainer.removeAllViews()
        diseaseContainerScrollView.scrollTo(0, 0)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // 해당 날짜의 takePillData가 없다면
        if (mainViewModel.takePillData.value?.get(targetDate) == null) {
            val groupMemberContent: FrameLayout =
                inflater.inflate(R.layout.activity_no_take_pill, null) as FrameLayout
            layoutParams.setMargins(32, 0, 32, 32)
            groupMemberContent.layoutParams = layoutParams
            diseaseContainer.addView(groupMemberContent)
            return
        }

        val targetTakePillData = mainViewModel.takePillData.value?.get(targetDate) as LinkedHashMap
        val numGroupMember = targetTakePillData?.size ?: 0

        var medicationDTO: MedicationInfoDTO? = MedicationInfoDTO()

        for (i in 0 until numGroupMember) {

            val groupMemberName = targetTakePillData?.keys?.elementAt(i)
            val diseaseMap = targetTakePillData[groupMemberName] as LinkedHashMap

            val groupMemberContent: FrameLayout =
                inflater.inflate(R.layout.activity_pill_check, null) as FrameLayout

            val groupMemberDiseaseContainer: LinearLayout =
                groupMemberContent.findViewById(R.id.pill_check_disease_container)
            layoutParams.setMargins(32, 0, 32, 32)
            groupMemberContent.layoutParams = layoutParams

            val groupMemberNameTextView: TextView =
                groupMemberContent.findViewById(R.id.pill_check_group_member_name)
            groupMemberNameTextView.text = groupMemberName

            val numDisease = diseaseMap?.size ?: 0 // 추가할 disease 개수 - 각 그룹원의 질병 개수

            for (j in 0 until numDisease) {
                val diseaseName = diseaseMap.keys.elementAt(j)
                val pillMap = diseaseMap[diseaseName] as LinkedHashMap

                val groupMemberDiseaseContent: FrameLayout = inflater.inflate(
                    R.layout.activity_pill_check_disease_content,
                    null
                ) as FrameLayout
                val diseaseNameTextView: TextView =
                    groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_name)
                diseaseNameTextView.text = diseaseName

                val groupMemberDiseasePillFrame: LinearLayout =
                    groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_pill_container)
                val diseaseLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                diseaseLayoutParams.setMargins(32, 0, 32, 32)
                groupMemberDiseaseContent.layoutParams = diseaseLayoutParams

                val pillCheckButton: ToggleButton =
                    groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_btn)

                // TODO - 복용 미복용에 대한 서버와 통신 필요
                pillCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        // 토글 버튼이 선택된 상태인 경우 (미복용 상태로 전환)
                        pillCheckButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.pill_check_ok_icon,
                            0
                        )
                    } else {
                        // 토글 버튼이 선택되지 않은 상태인 경우 (복용 상태로 전환)
                        pillCheckButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.pill_check_no_icon,
                            0
                        )
                    }
                }

                val numPillContents = pillMap.size // prescriptionPillContent 개수 - 각 질병의 약 개수
                for (k in 0 until numPillContents) {
                    val pillName = pillMap.keys.elementAt(k)
                    medicationDTO = pillMap[pillName]

                    if (time != medicationDTO?.takePillTime)
                        continue

                    if (!isTotalCalendarMode && selectedGroupMember != medicationDTO?.groupMemberIndex)
                        continue

                    pillCheckButton.isChecked = medicationDTO?.takeCheck == true

                    val prescriptionPillContent: FrameLayout =
                        inflater.inflate(R.layout.activity_pill_item, null) as FrameLayout
                    val pillItemNameView: TextView =
                        prescriptionPillContent.findViewById(R.id.pill_item_name)
                    pillItemNameView.text = pillName

                    val pillLayoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    val pillImage: ImageView =
                        prescriptionPillContent.findViewById(R.id.pill_item_image)

                    // 이미지 경로 지정
                    val imageRef = storageRef.child(medicationDTO.pillNum + ".jpg")
                    // 이미지 다운로드 URL 가져오기
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // 다운로드 URL을 사용하여 이미지 설정
                        Glide.with(pillImage)
                            .load(uri)
                            .placeholder(R.drawable.a201412020003201)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(pillImage)
                    }.addOnFailureListener { exception ->
                        // 다운로드 실패 시 처리할 작업
                        Log.e("TAG", "이미지 다운로드 실패: ${exception.message}")
                    }

                    prescriptionPillContent.findViewById<TextView>(R.id.pill_item_feature_shape).text =
                        medicationDTO.pillShape
                    prescriptionPillContent.findViewById<TextView>(R.id.pill_item_feature_color).text =
                        medicationDTO.pillColor

                    pillLayoutParams.setMargins(32, 0, 32, 32)
                    prescriptionPillContent.layoutParams = pillLayoutParams

                    val pillDTO = PillDTO(
                        pillIndex = medicationDTO.pillIndex,
                        pillNum = medicationDTO.pillNum,
                        pillName = medicationDTO.pillName,
                        pillFrontWord = medicationDTO.pillFrontWord,
                        pillBackWord = medicationDTO.pillBackWord,
                        pillShape = medicationDTO.pillPrecaution,
                        pillColor = medicationDTO.pillEffect,
                        pillCategoryName = medicationDTO.pillCategoryName,
                        pillFormulation = medicationDTO.pillFormulation,
                        pillEffect = medicationDTO.pillPrecaution,
                        pillPrecaution = medicationDTO.pillPrecaution
                    )

                    prescriptionPillContent.setOnClickListener {
                        val intent = Intent(this, PillInformationActivity::class.java)
                        intent.putExtra("pillInformationData", pillDTO)
                        startActivity(intent)
                        overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
                    }
                    groupMemberDiseasePillFrame.addView(prescriptionPillContent)
                }

                if (time != medicationDTO?.takePillTime)
                    continue

                if (!isTotalCalendarMode && selectedGroupMember != medicationDTO?.groupMemberIndex)
                    continue

                groupMemberDiseaseContainer.addView(groupMemberDiseaseContent)
            }

            if (time != medicationDTO?.takePillTime)
                continue

            if (!isTotalCalendarMode && selectedGroupMember != medicationDTO?.groupMemberIndex)
                continue

            diseaseContainer.addView(groupMemberContent)
        }
    }

    private fun setSelectedTimeBtnByNowTime() {
        val takeTimeBtnGroup: RadioGroup = findViewById(R.id.time_btn_group)

        when (LocalDateTime.now().hour) {
            in 0 until 6 -> {
                selectedTimeBtn = 4
            }

            in 6 until 8 -> {
                selectedTimeBtn = 0
            }

            in 8 until 12 -> {
                selectedTimeBtn = 1
            }

            in 12 until 18 -> {
                selectedTimeBtn = 2
            }

            in 18 until 21 -> {
                selectedTimeBtn = 3
            }

            in 21 until 24 -> {
                selectedTimeBtn = 4
            }
        }

        val takeTimeBtn = takeTimeBtnGroup.getChildAt(selectedTimeBtn) as RadioButton
        takeTimeBtn.isChecked = true
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

}