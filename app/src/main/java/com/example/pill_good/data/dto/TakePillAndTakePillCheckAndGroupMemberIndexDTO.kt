package com.example.pill_good.data.dto

data class TakePillAndTakePillCheckAndGroupMemberIndexDTO(
    val groupMemberIndex: Long,
    val takePillAndTakePillCheckDTOs: List<TakePillAndTakePillCheckDTO>
)
