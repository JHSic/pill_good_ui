package com.example.pill_good.data.dto

data class MedicationInfoDTO(
    val groupMemberIndex: Long? = null,
    val groupMemberName: String? = null,
    val pillIndex: Long? = null,
    val pillName: String? = null,
    val diseaseIndex: Long? = null,
    val diseaseName: String? = null,
    val takePillCheckIndex: Long? = null,
    val takeCheck: Boolean? = null,
    val takePillTime: Int? = null
)
