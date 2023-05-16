package com.example.pill_good.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.widget.Toolbar
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
        calendar = findViewById(R.id.calendar)
        calendar.setSelectedDate(CalendarDay.today())
        calendar.addDecorator(TodayDecorator())

        calendar.setOnDateChangedListener(object: OnDateSelectedListener {
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                calendar.addDecorator(EventDecorator(Collections.singleton(date)))
            }
        })

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