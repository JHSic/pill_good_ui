package com.example.pill_good.data.dto

import java.time.LocalDate

data class TakePillAndTakePillCheckDTO (
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