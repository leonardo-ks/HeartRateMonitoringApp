package com.example.heartratemonitoringapp.app.splashscreen

import androidx.lifecycle.ViewModel
import com.example.heartratemonitoringapp.domain.usecase.IUseCase

class SplashViewModel(private val useCase: IUseCase) : ViewModel() {
    val isLogin = useCase.getLoginState()
    val latestLoginDate = useCase.getLatestLoginDate()
}