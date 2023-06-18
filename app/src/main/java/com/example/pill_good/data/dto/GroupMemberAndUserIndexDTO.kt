package com.example.pill_good.data.dto

import java.time.LocalDate

data class GroupMemberAndUserIndexDTO(
    val groupMemberIndex: Long? = null,
    val userIndex: Long? = null,
    val groupMemberName: String? = null,
    val groupMemberBirth: LocalDate? = null,
    val groupMemberPhone: String? = null,
    val messageCheck: Boolean? = null
)