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

    @FormUrlEncoded
    @POST("add-contact")
    suspend fun addContact(
        @Header("Authorization")
        bearer: String,
        @Field (value = "contact")
        contact: Int,
    ): BasicResponse

    @FormUrlEncoded
    @POST("notification")
    suspend fun sendNotification(
        @Header("Authorization")
        bearer: String,
        @Field (value = "status")
        contact: Int,
    ): BasicResponse

    @FormUrlEncoded
    @POST("delete-contact")
    suspend fun deleteContact(
        @Header("Authorization")
        bearer: String,
        @Field (value = "contact")
        contact: Int,
    ): BasicResponse

    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization")
        bearer: String,
    ): ProfileResponse

    @GET("contact")
    suspend fun getContacts(
        @Header("Authorization")
        bearer: String,
    ): ListUserResponse

    @GET("search/{param}")
    suspend fun search(
        @Header("Authorization")
        bearer: String,
        @Path("param")
        param: String,
    ): ListUserResponse

    @GET("data")
    suspend fun getUserData(
        @Header("Authorization")
        bearer: String,
    ): UserMonitoringDataResponse

    @GET("data/{start}/{end}")
    suspend fun getUserDataByDate(
        @Header("Authorization")
        bearer: String,
        @Path("start")
        start: String,
        @Path("end")
        end: String
    ): UserMonitoringDataResponse

    @GET("data/{id}/{start}/{end}")
    suspend fun getUserDataByDateById(
        @Header("Authorization")
        bearer: String,
        @Path("id")
        id: Int,
        @Path("start")
        start: String,
        @Path("end")
        end: String
    ): UserMonitoringDataResponse

    @GET("average")
    suspend fun getAverageData(
        @Header("Authorization")
        bearer: String,
    ): AverageResponse

    @GET("average/{id}")
    suspend fun getAverageDataById(
        @Header("Authorization")
        bearer: String,
        @Path("id")
        id: Int
    ): AverageResponse

    @GET("limit")
    suspend fun getLimit(
        @Header("Authorization")
        bearer: String
    ): LimitResponse

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
        gender: Int,
        @Query("height")
        height: Int,
        @Query("weight")
        weight: Int
    ): UserDataUpdateResponse
}