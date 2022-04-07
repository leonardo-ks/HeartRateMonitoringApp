package com.example.heartratemonitoringapp.data

import com.example.heartratemonitoringapp.data.source.local.LocalDataSource
import com.example.heartratemonitoringapp.data.source.remote.RemoteDataSource
import com.example.heartratemonitoringapp.data.source.remote.network.ApiResponse
import com.example.heartratemonitoringapp.domain.repository.IRepository
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
import com.example.heartratemonitoringapp.util.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class Repository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): IRepository {

    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> =
        flow {
            emit(Resource.Loading())
            localDataSource.setUserEmail(email)
            when (val apiResponse = remoteDataSource.login(email, password).first()) {
                is ApiResponse.Success -> {
                    localDataSource.setBearer(apiResponse.data.accessToken)
                    localDataSource.setLoginState(true)
                    emit(Resource.Success(apiResponse.data.toDomain()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(LoginDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
        }
    }

    override fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<UserDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.register(name, email, password).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        localDataSource.setUserEmail(apiResponse.data.data.email)
                        emit(Resource.Success(apiResponse.data.data.toDomain()))
                    } else {
                        emit(Resource.Success(UserDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(UserDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun logout(bearer: String): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.logout(bearer).first()) {
                is ApiResponse.Success -> {
                    localDataSource.setBearer(null)
                    localDataSource.setLoginState(false)
                    emit(Resource.Success(apiResponse.data.message.toString()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(""))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun addData(
        bearer: String,
        avgHeartRate: Int,
        avgStep: Int,
        label: String
    ): Flow<Resource<MonitoringDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.addData(bearer, avgHeartRate, avgStep, label).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.toDomain()))
                    } else {
                        emit(Resource.Success(MonitoringDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(MonitoringDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getProfile(bearer: String): Flow<Resource<UserDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getProfile(bearer).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.profile != null) {
                        emit(Resource.Success(apiResponse.data.profile.toDomain()))
                    } else {
                        emit(Resource.Success(UserDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(UserDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserMonitoringData(bearer).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.toDomain()))
                    } else {
                        emit(Resource.Success(listOf(MonitoringDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(MonitoringDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getAverageData(bearer: String): Flow<Resource<AverageDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getAverageData(bearer).first()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(apiResponse.data.toDomain()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(AverageDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun deleteData(bearer: String, id: Int): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.deleteData(bearer, id).first()) {
                is ApiResponse.Success -> {
                    localDataSource.setBearer(null)
                    emit(Resource.Success(apiResponse.data.message.toString()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(""))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun updateMonitoringData(
        bearer: String,
        avgHeartRate: Int,
        avgStep: Int,
        label: String
    ): Flow<Resource<MonitoringDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.updateMonitoringData(bearer, avgHeartRate, avgStep, label).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.toDomain()))
                    } else {
                        emit(Resource.Success(MonitoringDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(MonitoringDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun updateUser(bearer: String, name: String): Flow<Resource<UserDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.updateUser(bearer, name).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.toDomain()))
                    } else {
                        emit(Resource.Success(UserDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(UserDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun setBearer(bearer: String) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.setBearer(bearer)
        }
    }

    override fun getBearer() {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.getBearer()
        }    }

    override fun setUserEmail(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.setUserEmail(email)
        }
    }

    override fun getUserEmail() {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.getUserEmail()
        }
    }

    override fun setLoginState(state: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.setLoginState(state)
        }
    }

    override fun getLoginState() {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.getLoginState()
        }
    }
}