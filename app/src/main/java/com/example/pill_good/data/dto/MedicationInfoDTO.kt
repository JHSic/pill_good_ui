package com.example.pill_good.data.dto

data class MedicationInfoDTO(
    val groupMemberIndex: Long? = null,
    val groupMemberName: String? = null,
    val pillIndex: Long? = null,
    val pillNum: String? = null,
    val pillName: String? = null,
    val pillFrontWord: String? = null,
    val pillBackWord: String? = null,
    val pillShape: String? = null,
    val pillColor: String? = null,
    val pillCategoryName: String? = null,
    val pillFormulation: String? = null,
    val pillEffect: String? = null,
    val pillPrecaution: String? = null,
    val diseaseIndex: Long? = null,
    val diseaseName: String? = null,
    val takePillCheckIndex: Long? = null,
    val takeCheck: Boolean? = null,
    val takePillTime: Int? = null
)
