package com.example.core.data.source.remote

import android.util.Log
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.data.source.remote.network.ApiService
import com.example.core.data.source.remote.response.*
import com.example.core.util.getMessage
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

    suspend fun getUserMonitoringDataByDate(bearer: String, start: String, end: String): Flow<ApiResponse<UserMonitoringDataResponse>> =
        flow {
            val response = apiService.getUserDataByDate(bearer, start, end)
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

    suspend fun getUserMonitoringDataByDateById(bearer: String, id: Int, start: String, end: String): Flow<ApiResponse<UserMonitoringDataResponse>> =
        flow {
            val response = apiService.getUserDataByDate(bearer, start, end)
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

    suspend fun getLimit(bearer: String): Flow<ApiResponse<LimitResponse>> =
        flow {
            val response = apiService.getLimit(bearer)
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

    suspend fun getContacts(bearer: String): Flow<ApiResponse<ListUserResponse>> =
        flow {
            val response = apiService.getContacts(bearer)
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

    suspend fun search(bearer: String, param: String): Flow<ApiResponse<ListUserResponse>> =
        flow {
            val response = apiService.search(bearer, param)
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

    suspend fun deleteContact(bearer: String, contact: Int): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.deleteContact(bearer, contact)
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

    suspend fun getAverageDataById(bearer: String, id: Int): Flow<ApiResponse<AverageResponse>> =
        flow {
            val response = apiService.getAverageDataById(bearer, id)
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

    suspend fun updateUser(bearer: String, name: String, email: String, dob: String, gender: Int, height: Int, weight: Int): Flow<ApiResponse<UserDataUpdateResponse>> =
        flow {
            val response = apiService.updateUser(bearer, name, email, dob, gender, height, weight)
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

    suspend fun addContact(bearer: String, contact: Int): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.addContact(bearer, contact)
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

    suspend fun sendNotification(bearer: String, status: Int, vibrate: Boolean): Flow<ApiResponse<BasicResponse>> =
        flow {
            val response = apiService.sendNotification(bearer, status, vibrate)
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