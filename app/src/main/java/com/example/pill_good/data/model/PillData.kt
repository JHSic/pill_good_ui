package com.example.pill_good.data.model

import java.io.Serializable

data class PillData(
    val pillName: String?,
    val takeCount: String?,
    val takeDay: String?,
    val takePillTimeList: List<Int>
) : Serializable