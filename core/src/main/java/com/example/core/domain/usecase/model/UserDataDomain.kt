package com.example.core.domain.usecase.model

data class UserDataDomain(
    val id: Int? = null,
    val email: String? = null,
    val name: String? = null,
    val profile: String? = null,
    val dob: String? = null,
    val gender: Int? = null,
    val height: Int? = null,
    val weight: Int? = null
)
