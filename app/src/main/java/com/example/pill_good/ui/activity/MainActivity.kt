package com.example.pill_good.ui.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.view.LayoutInflater
import android.widget.*
import com.example.pill_good.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.util.*

class MainActivity : CustomActionBarActivity() {
    lateinit var calendar: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addCustomView(R.layout.activity_main)

        val inflater = LayoutInflater.from(this)

        // 캘린더 버튼, 버튼 미지정 설정 및 캘린더 버튼 alpha 변경
        val calendarButton: ImageButton = findViewById(R.id.calendar_button)
        val logoText: TextView = findViewById(R.id.logo)
        calendarButton.alpha = 1f
        calendarButton.isEnabled = false
        logoText.isEnabled = false

        calendar = findViewById(R.id.calendar)
        calendar.setSelectedDate(CalendarDay.today())
        calendar.addDecorator(TodayDecorator())

        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                calendar.addDecorator(EventDecorator(Collections.singleton(date)))
            }
        })

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
        calendarRadioGroup.setOnCheckedChangeListener{ radioGroup, btnId ->
            when(btnId){
                // 전체 캘린더 선택 시 로직
                R.id.calendar_all -> {
                    allCalendarSelect()
                }
                /* 그룹원 캘린더 선택 시 로직
                   스피너를 캘린더 이전에 추가
                */
                R.id.calendar_group_member -> {
                    groupMemberCalenderSelect()
                }
            }
        }
    }

    // 전체 캘린더 선택 시 로직
    fun allCalendarSelect(){
        val groupMemberSelectorFrame : FrameLayout = findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews();
    }

    fun groupMemberCalenderSelect(){
        val groupMemberSelectorFrame : FrameLayout = findViewById(R.id.group_member_selector_container)

        groupMemberSelectorFrame.removeAllViews();

        val inflater = LayoutInflater.from(this)

        val groupMemberSelectorContent: FrameLayout = inflater.inflate(R.layout.activity_group_member_selector, null) as FrameLayout
        val groupMemberSelectorParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        groupMemberSelectorContent.layoutParams = groupMemberSelectorParams
        groupMemberSelectorFrame.addView(groupMemberSelectorContent)
    }
}