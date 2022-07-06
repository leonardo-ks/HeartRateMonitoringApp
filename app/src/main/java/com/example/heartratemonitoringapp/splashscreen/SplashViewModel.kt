package com.example.heartratemonitoringapp.splashscreen

import androidx.lifecycle.ViewModel
import com.example.core.domain.usecase.IUseCase

class SplashViewModel(private val useCase: IUseCase) : ViewModel() {
    val isLogin = useCase.getLoginState()
    val latestLoginDate = useCase.getLatestLoginDate()
    fun getBearer() = useCase.getBearer()
    fun logout(bearer: String) = useCase.logout(bearer)
}