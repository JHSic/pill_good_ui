package com.example.pill_good.data.dto

import java.time.LocalDate

data class InitialCalendarAndTakePillsInfoDTO(
    val userIndex: Long? = null,
    val dateStart: LocalDate? = null,
    val dateCur: LocalDate? = null,
    val dateEnd: LocalDate? = null
)
