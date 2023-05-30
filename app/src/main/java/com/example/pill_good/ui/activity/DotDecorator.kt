package com.example.pill_good.ui.activity

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class DotDecorator(
    private val date: CalendarDay?,
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade) {
        // 해당 날짜를 꾸밀 데코레이션을 설정
        view?.addSpan(DotSpan(7F, Color.parseColor( "#9DD84B")))
    }
}