package com.example.pill_good.data.model

import java.time.LocalDate

data class CalendarData(
    val groupMemberId: Long? = null,
    val takePillIndex: Long? = null,
    val prescriptionIndex: Long? = null,
    val pillIndex: Long? = null,
    val takeDay: Int? = null,
    val takeCount: Int? = null,
    val takePillCheckIndex: Long? = null,
    val takeDate: LocalDate? = null,
    val takePillTime: Int? = null,
    val takeCheck: Boolean? = null
)
