package com.example.heartratemonitoringapp.di

import com.example.core.domain.usecase.IUseCase
import com.example.core.domain.usecase.Interactor
import com.example.heartratemonitoringapp.auth.login.LoginViewModel
import com.example.heartratemonitoringapp.auth.register.RegisterViewModel
import com.example.heartratemonitoringapp.dashboard.MainViewModel
import com.example.heartratemonitoringapp.dashboard.band.BandViewModel
import com.example.heartratemonitoringapp.dashboard.home.HomeViewModel
import com.example.heartratemonitoringapp.dashboard.profile.ProfileViewModel
import com.example.heartratemonitoringapp.dashboard.profile.contact.ContactViewModel
import com.example.heartratemonitoringapp.dashboard.profile.contact.add.AddContactViewModel
import com.example.heartratemonitoringapp.dashboard.profile.contact.data.ShowDataViewModel
import com.example.heartratemonitoringapp.dashboard.profile.editpassword.EditPasswordViewModel
import com.example.heartratemonitoringapp.dashboard.profile.editprofile.EditProfileViewModel
import com.example.heartratemonitoringapp.dashboard.profile.newprofile.NewProfileViewModel
import com.example.heartratemonitoringapp.form.FormViewModel
import com.example.heartratemonitoringapp.monitoring.live.LiveMonitoringViewModel
import com.example.heartratemonitoringapp.splashscreen.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<IUseCase> { Interactor(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { BandViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { LiveMonitoringViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { EditPasswordViewModel(get()) }
    viewModel { FormViewModel(get()) }
    viewModel { ContactViewModel(get()) }
    viewModel { AddContactViewModel(get()) }
    viewModel { NewProfileViewModel(get()) }
    viewModel { ShowDataViewModel(get()) }
}