package com.example.heartratemonitoringapp.data.source.remote.network

import com.example.heartratemonitoringapp.data.source.remote.response.*
import retrofit2.http.*

interface ApiService {
    @POST("/login")
    suspend fun login(
        @Body
        email: String,
        password: String
    ): LoginResponse

    @POST("/logout")
    suspend fun logout(
        @Header("Authorization")
        bearer: String,
    ): LogoutResponse

    @POST("/register")
    suspend fun register(
        @Body
        name: String,
        email: String,
        password: String,
    ): RegisterResponse

    @POST("/data")
    suspend fun addData(
        @Header("Authorization")
        bearer: String,
        @Body
        avgHeartRate: Int,
        avgStep: Int,
        label: String,
    ): StoreMonitoringDataResponse

    @GET("/profile")
    suspend fun getProfile(
        @Header("Authorization")
        bearer: String,
    ): ProfileResponse

    @GET("/data")
    suspend fun getUserData(
        @Header("Authorization")
        bearer: String,
    ): UserMonitoringDataResponse

    @GET("/average")
    suspend fun getaverageData(
        @Header("Authorization")
        bearer: String,
    ): AverageResponse

    @DELETE("/data/{id}")
    suspend fun deleteData(
        @Header("Authorization")
        bearer: String,
        @Path("id")
        id: Int
    ): DeleteResponse

    @PUT("/data/{id}")
    suspend fun updateMonitoringData(
        @Header("Authorization")
        bearer: String,
        @Query("avg_heart_rate")
        avgHeartRate: Int,
        @Query("avg_step")
        avgStep: Int,
        @Query("label")
        label: String
    ): MonitoringDataUpdateResponse

    @PUT("/update-user")
    suspend fun updateUser(
        @Header("Authorization")
        bearer: String,
        @Query("name")
        name: String
    ): UserDataUpdateResponse
}