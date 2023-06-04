package com.example.pill_good.data.dto

import java.io.Serializable


data class PillScheduleDTO (
    var pillName: String? = null,
    var takeDay: Int? = null,
    var takeCount: Int? = null,
    var takePillTimeList: List<Int> = ArrayList()
) : Serializable