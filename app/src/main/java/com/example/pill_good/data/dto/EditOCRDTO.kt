package com.example.pill_good.data.dto

import java.io.Serializable
import java.time.LocalDate

data class EditOCRDTO(
    var groupMemberIndex: Long? = null,
    var groupMemberName: String? = null,
    var startDate: LocalDate? = null,
    var hospitalName: String? = null,
    var phoneNumber: String? = null,
    var diseaseCode: String? = null,
    var pillList: List<PillScheduleDTO> = ArrayList()
) : Serializable