package com.example.pill_good.data.dto

data class TakePillDTO(
    val takePillIndex: Long? = null,
    val prescriptionIndex: Long? = null,
    val pillIndex: Long? = null,
    val takeDay: Int? = null,
    val takeCount: Int? = null
)
