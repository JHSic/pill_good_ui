package com.example.pill_good.data.model

import java.io.Serializable

data class PrescriptionData(
    val groupMemberName: String?,
    val hospitalName: String?,
    val hospitalPhone: String?,
    val diseaseCode: String?,
    val pillDataList: List<PillData>
) : Serializable
