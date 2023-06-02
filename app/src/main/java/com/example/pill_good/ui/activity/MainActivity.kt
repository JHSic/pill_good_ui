package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.pill_good.R
import com.example.pill_good.data.model.SpinnerData
import com.example.pill_good.ui.viewmodel.MainViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
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
    private var isTotalCalendarMode: Boolean = true

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
            val calendarRadioGroup : RadioGroup = findViewById(R.id.calendar_radio_group)

            if(buttonId == calendarRadioGroup.getChildAt(0).id) {
                setDecorators(currentMonth, isReplaceDecorator = true)
            } else {
                setDecorators(currentMonth, isReplaceDecorator = true, isGroupMemberDecorator = true)
            }
        }

        mainViewModel.groupMemberList.observe(this) {

        }

        mainViewModel.takePillData.observe(this) {

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
        val calendarRadioGroup : RadioGroup = findViewById(R.id.calendar_radio_group)
        buttonId = calendarRadioGroup.checkedRadioButtonId

        // 데코레이터 설정
        decorators = arrayListOf()
        setDecorators(LocalDate.now().monthValue - 1)

        // onDateChangedListener 설정
        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                // TODO("Not Implementation: 날짜 선택 시 복용 현황 화면이 위로 올라오게끔 구현")
                // mainViewModel.testAddCalendarData()
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

            val calendarRadioGroup : RadioGroup = findViewById(R.id.calendar_radio_group)
            if(buttonId == calendarRadioGroup.getChildAt(0).id) {
                setDecorators(this.currentMonth, isReplaceDecorator = true)
            } else {
                setDecorators(this.currentMonth, isReplaceDecorator = true, isGroupMemberDecorator = true)
            }
        }

        // 라디오 그룹에 대한 설정
        initializeCalendarRadioGroup()

        // 복약 시간 형식에 따라 라디오 그룹에 대한 함수화 예정
        val diseaseContainer: LinearLayout = findViewById(R.id.group_member_disease_container)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val numGroupMember = 2 // 추가할 groupMember 개수 - 그룹원 개수
        for (i in 1..numGroupMember) {
            val groupMemberContent: FrameLayout = inflater.inflate(R.layout.activity_pill_check, null) as FrameLayout
            val groupMemberDiseaseContainer: LinearLayout = groupMemberContent.findViewById(R.id.pill_check_disease_container)
            layoutParams.setMargins(32, 0, 32, 32)
            groupMemberContent.layoutParams = layoutParams

            val numDisease = 2 // 추가할 disease 개수 - 각 그룹원의 질병 개수
            for (j in 1..numDisease) {
                val groupMemberDiseaseContent: FrameLayout = inflater.inflate(R.layout.activity_pill_check_disease_content, null) as FrameLayout
                val groupMemberDiseasePillFrame: LinearLayout = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_pill_container)
                val diseaseLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                diseaseLayoutParams.setMargins(32, 0, 32, 32)
                groupMemberDiseaseContent.layoutParams = diseaseLayoutParams

                val pillCheckButton : ToggleButton = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_btn)

                pillCheckButton.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        // 토글 버튼이 선택된 상태인 경우
                        pillCheckButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pill_check_ok_icon, 0)
                    } else {
                        // 토글 버튼이 선택되지 않은 상태인 경우
                        pillCheckButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pill_check_no_icon, 0)
                    }
                }
                val numPillContents = 3 // prescriptionPillContent 개수 - 각 질병의 약 개수
                for (k in 1..numPillContents) {
                    val prescriptionPillContent: FrameLayout = inflater.inflate(R.layout.activity_pill_item, null) as FrameLayout
                    val pillLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    pillLayoutParams.setMargins(32, 0, 32, 32)
                    prescriptionPillContent.layoutParams = pillLayoutParams

                    prescriptionPillContent.setOnClickListener{
                        val intent = Intent(this,pillInformationActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(0, 0) // 화면 전환 애니메이션 제거
                    }
                    groupMemberDiseasePillFrame.addView(prescriptionPillContent)
                }
                groupMemberDiseaseContainer.addView(groupMemberDiseaseContent)
            }
            diseaseContainer.addView(groupMemberContent)
        }
    }

    // 캘린더 선택 라디오 버튼 그룹 생성
    fun initializeCalendarRadioGroup(){
        val calendarRadioGroup : RadioGroup = findViewById(R.id.calendar_radio_group)
        calendarRadioGroup.setOnCheckedChangeListener{ _, btnId ->
            buttonId = btnId
            when(buttonId){
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
    private fun allCalendarSelect(){
        val groupMemberSelectorFrame : FrameLayout = findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews()

        // 데코레이터 설정
        setDecorators(currentMonth, isReplaceDecorator = true)
    }

    private fun groupMemberCalenderSelect(){
        val groupMemberSelectorFrame : FrameLayout = findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews()

        val inflater = LayoutInflater.from(this)

        // Spinner 설정
        val groupMemberSelectorContent: FrameLayout = inflater.inflate(R.layout.activity_group_member_selector, null) as FrameLayout
        val groupMemberSelectorParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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
        if(selectedGroupMember == null && mainViewModel.groupMemberList.value != null) {
            selectedGroupMember = mainViewModel.groupMemberList.value!!.first().groupMemberIndex
            selectedSpinnerIndex = 0

        }

        // 이전에 저장한 인덱스의 그룹 멤버 캘린더 조회
        groupMemberSpinner.setSelection(selectedSpinnerIndex!!)

        // Spinner onClickListener 설정
        groupMemberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 아이템이 선택되었을 때의 동작을 구현
                val selectedItem = parent?.getItemAtPosition(position) as SpinnerData
                selectedGroupMember = selectedItem.id
                selectedSpinnerIndex = position

                // 선택된 아이템에 대한 처리를 수행합니다.
                if(selectedGroupMember != null)
                    setDecorators(currentMonth, isReplaceDecorator = true, isGroupMemberDecorator = true)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // 데코레이터 설정
        setDecorators(currentMonth, isReplaceDecorator = true, isGroupMemberDecorator = true)
    }

    /**
     * TODO 캘린더의 데코레이터를 설정해주는 메소드
     * Boolean값을 이용해 Decorator의 상태 및 전체 or 그룹원 캘린더에 대한 데코레이터를 설정할 수 있게끔 구현하였음
     *
     * @param curMonth Decorator를 적용하는 기준이 되는 month. View에 표현되는 해당 month의 day와 그 외 day를 구분하기 위해 사용
     * @param isReplaceDecorator 기존 Decorator를 대체하는지에 대한 여부를 받음
     * @param isGroupMemberDecorator 전체 캘린더인지 그룹원 캘린더인지에 대한 구분을 위해 사용
     */
    private fun setDecorators(month: Int, isReplaceDecorator: Boolean = false, isGroupMemberDecorator: Boolean = false) {
        if(isReplaceDecorator) {
            val newDecorators = arrayListOf<DayViewDecorator>()

            // 기존 데코레이터 중 NotDayOfMonthDecorator를 제외한 데코레이터들을 새로운 리스트에 추가
            decorators.forEach { decorator ->
                if(isTotalCalendarMode) {
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
                if(!isGroupMemberDecorator || selectedGroupMember != null && it.key == selectedGroupMember) {   // NullCheck 필요없을수도
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
                it.value.forEach {it1 ->
                    decorators.add(DotDecorator(CalendarDay.from(convertLocalDateToDate(it1.key))))
                }
            }

            calendar.addDecorators(decorators)
        }
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
}