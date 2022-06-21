package com.example.heartratemonitoringapp.data.source.remote.network

import com.example.heartratemonitoringapp.data.source.remote.response.BasicResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface FirebaseService {
    @POST("send")
    suspend fun logout(
        @Header("Authorization")
        bearer: String,
    ): BasicResponse
}