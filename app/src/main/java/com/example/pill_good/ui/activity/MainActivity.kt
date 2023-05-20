package com.example.pill_good.ui.activity

import android.os.Bundle
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

        calendar = findViewById(R.id.calendar)
        calendar.setSelectedDate(CalendarDay.today())
        calendar.addDecorator(TodayDecorator())

        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                calendar.addDecorator(EventDecorator(Collections.singleton(date)))
            }
        })

        val diseaseContainer: LinearLayout = findViewById(R.id.group_member_disease_container)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val numGroupMember = 2 // 추가할 groupMember 개수 - 그룹원 개수
        for (i in 1..numGroupMember) {
            val groupMemberContent: FrameLayout = inflater.inflate(R.layout.activity_pill_check, null) as FrameLayout
            val groupMemberDiseaseContainer: LinearLayout = groupMemberContent.findViewById(R.id.pill_check_disease_container)
            layoutParams.setMargins(32, 0, 32, 32) // 아래쪽에 16dp의 마진
            groupMemberContent.layoutParams = layoutParams

            val numDisease = 2 // 추가할 disease 개수 - 각 그룹원의 질병 개수
            for (j in 1..numDisease) {
                val groupMemberDiseaseContent: FrameLayout = inflater.inflate(R.layout.activity_pill_check_disease_content, null) as FrameLayout
                val groupMemberDiseasePillFrame: LinearLayout = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_pill_container)
                val diseaseLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                diseaseLayoutParams.setMargins(32, 0, 32, 32) // 아래쪽에 16dp의 마진
                groupMemberDiseaseContent.layoutParams = diseaseLayoutParams

                val pillCheckButton : ToggleButton = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_btn)

                pillCheckButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        // The toggle is enabled
                    } else {
                        // The toggle is disabled
                    }
                }

//                val pillCheckDiseaseEatButton: MaterialButton = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_eat)
//                val pillCheckDiseaseNotEatButton: MaterialButton = groupMemberDiseaseContent.findViewById(R.id.pill_check_disease_not_eat)

//                pillCheckDiseaseEatButton.addOnCheckedChangeListener { _, isChecked ->
//                    if (isChecked) {
//                        pillCheckDiseaseEatButton.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
//                        pillCheckDiseaseEatButton.setTextColor(ContextCompat.getColor(this, R.color.white))
//                        pillCheckDiseaseNotEatButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//                        pillCheckDiseaseNotEatButton.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    }
//                }
//
//                pillCheckDiseaseNotEatButton.addOnCheckedChangeListener { _, isChecked ->
//                    if (isChecked) {
//                        pillCheckDiseaseNotEatButton.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
//                        pillCheckDiseaseNotEatButton.setTextColor(ContextCompat.getColor(this, R.color.white))
//                        pillCheckDiseaseEatButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
//                        pillCheckDiseaseEatButton.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    }
//                }

                val numPillContents = 3 // prescriptionPillContent 개수 - 각 질병의 약 개수
                for (k in 1..numPillContents) {
                    val prescriptionPillContent: FrameLayout = inflater.inflate(R.layout.activity_pill_item, null) as FrameLayout
                    val pillLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    pillLayoutParams.setMargins(32, 0, 32, 32) // 아래쪽에 32dp의 마진
                    prescriptionPillContent.layoutParams = pillLayoutParams
                    groupMemberDiseasePillFrame.addView(prescriptionPillContent)
                }

                groupMemberDiseaseContainer.addView(groupMemberDiseaseContent)
            }

            diseaseContainer.addView(groupMemberContent)
        }


        // 각 버튼 클릭 시 해야하는 로직 구현 ->

//        var allGroupMemberCalendarButton : Button = findViewById(R.id.calendar_all)
//        var specificGroupMemberCalendarButton : Button = findViewById(R.id.calendar_group_member)
//
//        allGroupMemberCalendarButton.setOnClickListener{
//
//        }
//
//        specificGroupMemberCalendarButton.setOnClickListener {
//
//        }
    }
}