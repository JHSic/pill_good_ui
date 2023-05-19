package com.example.pill_good.data.dto

import java.time.LocalDate

data class PrescriptionDTO(
    val prescriptionIndex: Long? = null,
    val groupMemberIndex: Long? = null,
    val diseaseIndex: Long? = null,
    val prescriptionRegistrationDate: LocalDate? = null,
    val prescriptionDate: LocalDate? = null,
    val hospitalPhone: String? = null,
    val hospitalName: String? = null
)
