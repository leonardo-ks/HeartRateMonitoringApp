package com.example.heartratemonitoringapp.di

import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.Interactor
import com.example.heartratemonitoringapp.app.auth.login.LoginViewModel
import com.example.heartratemonitoringapp.app.auth.register.RegisterViewModel
import com.example.heartratemonitoringapp.app.dashboard.band.BandViewModel
import com.example.heartratemonitoringapp.app.dashboard.home.HomeViewModel
import com.example.heartratemonitoringapp.app.dashboard.profile.ProfileViewModel
import com.example.heartratemonitoringapp.app.dashboard.profile.editpassword.EditPasswordViewModel
import com.example.heartratemonitoringapp.app.dashboard.profile.editprofile.EditProfileViewModel
import com.example.heartratemonitoringapp.app.monitoring.live.LiveMonitoringViewModel
import com.example.heartratemonitoringapp.app.splashscreen.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<IUseCase> { Interactor(get()) }
}

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { BandViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { LiveMonitoringViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { EditPasswordViewModel(get()) }
}