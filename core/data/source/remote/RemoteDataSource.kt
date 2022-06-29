package com.example.heartratemonitoringapp.data.source.remote

import com.example.heartratemonitoringapp.data.source.remote.network.ApiResponse
import com.example.heartratemonitoringapp.data.source.remote.network.ApiService
import com.example.heartratemonitoringapp.data.source.remote.response.*
import com.example.heartratemonitoringapp.util.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

class RemoteDataSource(private val apiService: ApiService, ) {

    suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>> =
        flow {
            val response = apiService.login(email, password)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                    emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun register(name: String, email: String, password: String): Flow<ApiResponse<RegisterResponse>> =
        flow {
            val response = apiService.register(name, email, password)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                    emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun logout(bearer: String): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.logout(bearer)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                    emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun addData(bearer: String, avgHeartRate: Int, stepChanges: Int, step:Int, label: String?, createdAt: String?): Flow<ApiResponse<StoreMonitoringDataResponse>> =
        flow {
            val response = apiService.addData(bearer, avgHeartRate, stepChanges, step, label.toString(), createdAt.toString()
            )
            if (response.success) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun findData(bearer: String, avgHeartRate: Int, avgStep: Int): Flow<ApiResponse<FindDataResponse>> =
        flow {
            val response = apiService.findData(bearer, avgHeartRate, avgStep)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getProfile(bearer: String): Flow<ApiResponse<ProfileResponse>> =
        flow {
            val response = apiService.getProfile(bearer)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getUserMonitoringData(bearer: String): Flow<ApiResponse<UserMonitoringDataResponse>> =
        flow {
            val response = apiService.getUserData(bearer)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getAverageData(bearer: String): Flow<ApiResponse<AverageResponse>> =
        flow {
            val response = apiService.getAverageData(bearer)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun deleteData(bearer: String, id: Int): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.deleteData(bearer, id)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<ApiResponse<MonitoringDataUpdateResponse>> =
        flow {
            val response = apiService.updateMonitoringData(bearer, avgHeartRate, avgStep, label)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updateUser(bearer: String, name: String, email: String, dob: String, gender: Int): Flow<ApiResponse<UserDataUpdateResponse>> =
        flow {
            val response = apiService.updateUser(bearer, name, email, dob, gender)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun changePassword(bearer: String, old: String, new: String, confirmation: String): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.changePassword(bearer, old, new, confirmation)
            if (response.success == true) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Error(response.message.toString()))
            }
        }.catch { e ->
            when (e) {
                is HttpException -> {
                    val responseBody = e.response()?.errorBody()
                    emit(ApiResponse.Error("${e.code()}: ${responseBody?.getMessage()}"))
                } else -> {
                emit(ApiResponse.Error(e.message.toString()))
            }
            }
        }.flowOn(Dispatchers.IO)
}