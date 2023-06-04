package com.example.pill_good.data.model

data class SpinnerData(
    val id: Long?,
    val name: String?
) {
    override fun toString(): String {
        return name.toString()
    }
}