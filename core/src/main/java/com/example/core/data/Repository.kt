package com.example.core.data

import android.annotation.SuppressLint
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiResponse
import com.example.core.domain.repository.IRepository
import com.example.core.domain.usecase.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Repository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): IRepository {

    @SuppressLint("SimpleDateFormat")
    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.login(email, password).first()) {
                is ApiResponse.Success -> {
                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    val currentDate = sdf.format(Date())
                    localDataSource.setBearer(apiResponse.data.accessToken)
                    localDataSource.setLoginState(true)
                    localDataSource.setLatestLoginDate(currentDate)
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
        stepChanges: Int,
        step: Int,
        label: String?,
        createdAt: String?
    ): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.addData(bearer, avgHeartRate, stepChanges, step, label.toString(), createdAt.toString()).first()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(apiResponse.data.success))
                }
                is ApiResponse.Empty -> emit(Resource.Success(false))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun findData(
        bearer: String,
        avgHeartRate: Int,
        avgStep: Int
    ): Flow<Resource<List<String>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.findData(bearer, avgHeartRate, avgStep).first()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(apiResponse.data.labels ?: listOf()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getProfile(bearer: String): Flow<Resource<UserDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getProfile(bearer).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.profile != null) {
                        apiResponse.data.profile.id?.let { localDataSource.setUserId(it) }
                        emit(Resource.Success(apiResponse.data.profile.toDomain()))
                    } else {
                        emit(Resource.Success(UserDataDomain()))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(UserDataDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getLimit(bearer: String): Flow<Resource<LimitDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getLimit(bearer).first()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(apiResponse.data.toDomain()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(LimitDomain()))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserMonitoringData(bearer).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.map { data ->
                            data?.toDomain() ?: MonitoringDataDomain()
                        }))
                    } else {
                        emit(Resource.Success(listOf(MonitoringDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(MonitoringDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getUserMonitoringDataByDate(bearer: String, start: String, end: String): Flow<Resource<List<MonitoringDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserMonitoringDataByDate(bearer, start, end).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.map { data ->
                            data?.toDomain() ?: MonitoringDataDomain()
                        }))
                    } else {
                        emit(Resource.Success(listOf(MonitoringDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(MonitoringDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getUserMonitoringDataByDateById(bearer: String, id: Int, start: String, end: String): Flow<Resource<List<MonitoringDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getUserMonitoringDataByDateById(bearer, id, start, end).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.map { data ->
                            data?.toDomain() ?: MonitoringDataDomain()
                        }))
                    } else {
                        emit(Resource.Success(listOf(MonitoringDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(MonitoringDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun getContacts(bearer: String): Flow<Resource<List<UserDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getContacts(bearer).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.map { data ->
                            data?.toDomain() ?: UserDataDomain()
                        }))
                    } else {
                        emit(Resource.Success(listOf(UserDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(UserDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun search(bearer: String, param: String): Flow<Resource<List<UserDataDomain>>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.search(bearer, param).first()) {
                is ApiResponse.Success -> {
                    if (apiResponse.data.data != null) {
                        emit(Resource.Success(apiResponse.data.data.map { data ->
                            data?.toDomain() ?: UserDataDomain()
                        }))
                    } else {
                        emit(Resource.Success(listOf(UserDataDomain())))
                    }
                }
                is ApiResponse.Empty -> emit(Resource.Success(listOf(UserDataDomain())))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun deleteContact(bearer: String, contact: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = remoteDataSource.deleteContact(bearer, contact).first()) {
            is ApiResponse.Success -> {
                emit(Resource.Success(apiResponse.data.message.toString()))
            }
            is ApiResponse.Empty -> emit(Resource.Success(""))
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

    override fun getAverageDataById(bearer: String, id: Int): Flow<Resource<AverageDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.getAverageDataById(bearer, id).first()) {
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

    override fun updateUser(bearer: String, name: String, email: String, dob: String, gender: Int, height: Int, weight: Int): Flow<Resource<UserDataDomain>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.updateUser(bearer, name, email, dob, gender, height, weight).first()) {
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

    override fun changePassword(
        bearer: String,
        old: String,
        new: String,
        confirmation: String
    ): Flow<Resource<String>> =
        flow {
            emit(Resource.Loading())
            when (val apiResponse = remoteDataSource.changePassword(bearer, old, new, confirmation).first()) {
                is ApiResponse.Success -> {
                    emit(Resource.Success(apiResponse.data.message.toString()))
                }
                is ApiResponse.Empty -> emit(Resource.Success(""))
                is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
            }
        }

    override fun addContact(bearer: String, contact: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = remoteDataSource.addContact(bearer, contact).first()) {
            is ApiResponse.Success -> {
                emit(Resource.Success(apiResponse.data.message.toString()))
            }
            is ApiResponse.Empty -> emit(Resource.Success(""))
            is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
        }
    }

    override fun sendNotification(bearer: String, status: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = remoteDataSource.sendNotification(bearer, status).first()) {
            is ApiResponse.Success -> {
                emit(Resource.Success(apiResponse.data.message.toString()))
            }
            is ApiResponse.Empty -> emit(Resource.Success(""))
            is ApiResponse.Error -> emit(Resource.Error(apiResponse.errorMessage))
        }
    }

    override fun setBearer(bearer: String) = localDataSource.setBearer(bearer)

    override fun getBearer(): Flow<String?> = flow {
        emit(localDataSource.getBearer())
    }

    override fun setLoginState(state: Boolean) = localDataSource.setLoginState(state)

    override fun getLoginState() = flow {
        emit(localDataSource.getLoginState())
    }

    override fun setUserId(id: Int) = localDataSource.setUserId(id)

    override fun getUserId(): Flow<Int> = flow {
        emit(localDataSource.getUserId())
    }

    override fun setLatestLoginDate(date: String) = localDataSource.setLatestLoginDate(date)


    override fun getLatestLoginDate(): Flow<String?> = flow {
        emit(localDataSource.getLatestLoginDate())
    }

    override fun setMonitoringPeriod(period: Int) = localDataSource.setMonitoringPeriod(period)

    override fun getMonitoringPeriod(): Flow<Int> = flow {
        emit(localDataSource.getMonitoringPeriod())
    }

    override fun setBackgroundMonitoringState(state: Boolean) = localDataSource.setBackgroundMonitoringState(state)

    override fun getBackgroundMonitoringState(): Flow<Boolean> = flow {
        emit(localDataSource.getBackgroundMonitoringState())
    }

    override fun setMinHRLimit(min: Int) = localDataSource.setMinHRLimit(min)

    override fun getMinHRLimit(): Flow<Int> = flow {
        emit(localDataSource.getMinHRLimit())
    }

    override fun setMaxHRLimit(max: Int) = localDataSource.setMaxHRLimit(max)

    override fun getMaxHRLimit(): Flow<Int> = flow {
        emit(localDataSource.getMaxHRLimit())
    }

    override fun setAnomalyDetectedTimes(times: Int) = localDataSource.setAnomalyDetectedTimes(times)

    override fun getAnomalyDetectedTimes(): Flow<Int> = flow {
        emit(localDataSource.getAnomalyDetectedTimes())
    }

    override fun setLatestAnomalyDate(date: String) = localDataSource.setLatestLoginDate(date)

    override fun getLatestAnomalyDate(): Flow<String?> = flow {
        emit(localDataSource.getLatestLoginDate())
    }

    override fun getMonitoringDataList(): List<MonitoringDataDomain> {
        val domain = arrayListOf<MonitoringDataDomain>()
            localDataSource.getMonitoringDataList().map {
                domain.add(it.toDomain())
            }
        return domain
    }

    override fun deleteMonitoringDataById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteMonitoringDataById(id)
        }
    }

    override fun deleteMonitoringDataByDate(date: String) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.deleteMonitoringDataByDate(date)
        }
    }

    override fun insertMonitoringData(monitoringData: MonitoringDataDomain) {
        CoroutineScope(Dispatchers.IO).launch {
            localDataSource.insertMonitoringData(monitoringData.toEntities())
        }
    }

}