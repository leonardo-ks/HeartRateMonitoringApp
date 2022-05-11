package com.example.heartratemonitoringapp.domain.usecase

import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.domain.repository.IRepository
import com.example.heartratemonitoringapp.domain.usecase.model.AverageDomain
import com.example.heartratemonitoringapp.domain.usecase.model.LoginDomain
import com.example.heartratemonitoringapp.domain.usecase.model.MonitoringDataDomain
import com.example.heartratemonitoringapp.domain.usecase.model.UserDataDomain
import kotlinx.coroutines.flow.Flow

class Interactor(private val repository: IRepository): IUseCase {
    override fun login(email: String, password: String): Flow<Resource<LoginDomain>> = repository.login(email, password)
    override fun register(name: String, email: String, password: String): Flow<Resource<UserDataDomain>> = repository.register(name, email, password)
    override fun logout(bearer: String): Flow<Resource<String>> = repository.logout(bearer)
    override fun addData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>> = repository.addData(bearer, avgHeartRate, avgStep, label)
    override fun findData(bearer: String, avgHeartRate: Int, avgStep: Int): Flow<Resource<List<String>>> = repository.findData(bearer, avgHeartRate, avgStep)
    override fun getProfile(bearer: String): Flow<Resource<UserDataDomain>> = repository.getProfile(bearer)
    override fun getUserMonitoringData(bearer: String): Flow<Resource<List<MonitoringDataDomain>>> = repository.getUserMonitoringData(bearer)
    override fun getAverageData(bearer: String): Flow<Resource<AverageDomain>> = repository.getAverageData(bearer)
    override fun deleteData(bearer: String, id: Int): Flow<Resource<String>> = repository.deleteData(bearer, id)
    override fun updateMonitoringData(bearer: String, avgHeartRate: Int, avgStep: Int, label: String): Flow<Resource<MonitoringDataDomain>> = repository.updateMonitoringData(bearer, avgHeartRate, avgStep, label)
    override fun updateUser(bearer: String, name: String): Flow<Resource<UserDataDomain>> = repository.updateUser(bearer, name)
    override fun setBearer(bearer: String) = repository.setBearer(bearer)
    override fun getBearer(): Flow<String?> = repository.getBearer()
    override fun setLoginState(state: Boolean) = repository.setLoginState(state)
    override fun getLoginState(): Flow<Boolean> = repository.getLoginState()
    override fun setLatestLoginDate(date: String) = repository.setLatestLoginDate(date)
    override fun getLatestLoginDate(): Flow<String?> = repository.getLatestLoginDate()
}