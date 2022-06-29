package com.example.core.data.source.remote.network

import com.example.core.data.source.remote.response.*
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field (value = "email")
        email: String,
        @Field (value = "password")
        password: String
    ): LoginResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization")
        bearer: String,
    ): BasicResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field (value = "name")
        name: String,
        @Field (value = "email")
        email: String,
        @Field (value = "password")
        password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("data")
    suspend fun addData(
        @Header("Authorization")
        bearer: String,
        @Field (value = "avg_heart_rate")
        avgHeartRate: Int,
        @Field (value = "step_changes")
        stepChanges: Int,
        @Field (value = "step")
        step: Int,
        @Field (value = "label")
        label: String,
        @Field (value = "created_at")
        createdAt: String
    ): StoreMonitoringDataResponse

    @FormUrlEncoded
    @POST("find-data")
    suspend fun findData(
        @Header("Authorization")
        bearer: String,
        @Field (value = "avg_heart_rate")
        avgHeartRate: Int,
        @Field (value = "step_changes")
        stepChanges: Int,
    ): FindDataResponse

    @FormUrlEncoded
    @POST("change-password")
    suspend fun changePassword(
        @Header("Authorization")
        bearer: String,
        @Field (value = "old_password")
        oldPassword: String,
        @Field (value = "new_password")
        newPassword: String,
        @Field (value = "new_password_confirmation")
        confirmation: String,
    ): BasicResponse

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization")
        bearer: String,
    ): ProfileResponse

    @GET("data")
    suspend fun getUserData(
        @Header("Authorization")
        bearer: String,
    ): UserMonitoringDataResponse

    @GET("average")
    suspend fun getAverageData(
        @Header("Authorization")
        bearer: String,
    ): AverageResponse

    @DELETE("data/{id}")
    suspend fun deleteData(
        @Header("Authorization")
        bearer: String,
        @Path("id")
        id: Int
    ): BasicResponse

    @PUT("data/{id}")
    suspend fun updateMonitoringData(
        @Header("Authorization")
        bearer: String,
        @Query("avg_heart_rate")
        avgHeartRate: Int,
        @Query("step_changes")
        stepChanges: Int,
        @Query("label")
        label: String
    ): MonitoringDataUpdateResponse

    @PUT("update-user")
    suspend fun updateUser(
        @Header("Authorization")
        bearer: String,
        @Query("name")
        name: String,
        @Query("email")
        email: String,
        @Query("dob")
        dob: String,
        @Query("gender")
        gender: Int
    ): UserDataUpdateResponse
}