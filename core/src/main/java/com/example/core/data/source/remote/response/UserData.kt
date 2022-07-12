package com.example.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class UserData (
    @field:SerializedName("profile")
    val profile: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("gender")
    val gender: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null,

    @field:SerializedName("weight")
    val weight: Int? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)