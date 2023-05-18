package com.example.pill_good.data.dto

import java.time.LocalDate

data class TakePillCheckAndGroupMemberIndexDTO(
    val userIndex: Long? = null,
    val takeDateStart: LocalDate? = null,
    val takeDateEnd: LocalDate? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)
