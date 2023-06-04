package com.example.pill_good.data.dto

import java.io.Serializable

data class PartiallyTakePillDTO(
    val pillName : String? = null,
    val takeDay : Int? = null,
    val takeCount : Int? = null
) : Serializable