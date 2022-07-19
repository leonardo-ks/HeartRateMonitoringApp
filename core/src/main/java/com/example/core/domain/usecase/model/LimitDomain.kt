package com.example.core.domain.usecase.model

data class LimitDomain (
    val lower: Int? = null,
    val upperStill: Int? = null,
    val upperWalk: Int? = null
)