package com.example.pill_good.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class NotDayOfMonthDecorator(private val selectedMonth: Int) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        // 해당 월에 속하는 일자만 데코레이팅하기 위해 검사
        return day?.month != selectedMonth
    }

    override fun decorate(view: DayViewFacade?) {
        val transparentColor = Color.parseColor("#80000000")
        val transparentForegroundColorSpan = ForegroundColorSpan(transparentColor)
        view?.addSpan(transparentForegroundColorSpan)
        view?.addSpan(StyleSpan(Typeface.ITALIC))
    }
}
