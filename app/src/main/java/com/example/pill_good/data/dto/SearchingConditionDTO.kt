package com.example.pill_good.data.dto

import java.io.Serializable

data class SearchingConditionDTO(
    val pillName: String? = null,
    val pillShape: String? = null,
    val pillColor: String? = null,
    val pillFrontWord: String? = null,
    val pillBackWord: String? = null
) : Serializable
