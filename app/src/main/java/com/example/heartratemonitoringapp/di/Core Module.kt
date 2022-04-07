package com.example.heartratemonitoringapp.di

import android.content.Context
import androidx.room.Room
import com.example.heartratemonitoringapp.data.Repository
import com.example.heartratemonitoringapp.data.source.local.LocalDataSource
import com.example.heartratemonitoringapp.data.source.local.room.Database
import com.example.heartratemonitoringapp.data.source.local.sharedpref.ISharedPreferences
import com.example.heartratemonitoringapp.data.source.local.sharedpref.SharedPrederences
import com.example.heartratemonitoringapp.data.source.local.sharedpref.SharedPref
import com.example.heartratemonitoringapp.data.source.remote.RemoteDataSource
import com.example.heartratemonitoringapp.data.source.remote.network.ApiService
import com.example.heartratemonitoringapp.domain.repository.IRepository
import com.example.heartratemonitoringapp.domain.usecase.IUseCase
import com.example.heartratemonitoringapp.domain.usecase.Interactor
import com.example.heartratemonitoringapp.ui.band.BandViewModel
import com.example.heartratemonitoringapp.ui.home.HomeViewModel
import com.example.heartratemonitoringapp.ui.login.LoginViewModel
import com.example.heartratemonitoringapp.ui.profile.ProfileViewModel
import com.example.heartratemonitoringapp.ui.register.RegisterViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val sharedPreferencesModule = module {
    single {
        androidContext().getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
    }
    single { SharedPref(get()) }
    single<ISharedPreferences> { SharedPrederences(get()) }
}

val databaseModule = module {
    factory { get<Database>().dao() }

    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            "heart_monitoring.db"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/api")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get(), get()) }
    single { RemoteDataSource(get()) }
    single<IRepository> { Repository(get(), get()) }
}

val useCaseModule = module {
    factory<IUseCase> { Interactor(get()) }
}

val viewModelModule = module {
    viewModel { BandViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
}