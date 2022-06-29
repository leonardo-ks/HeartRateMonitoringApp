package com.example.core.di

import android.content.Context
import androidx.room.Room
import com.example.core.data.Repository
import com.example.core.data.source.local.LocalDataSource
import com.example.core.data.source.local.room.Database
import com.example.core.data.source.local.sharedpref.ISharedPreferences
import com.example.core.data.source.local.sharedpref.SharedPref
import com.example.core.data.source.local.sharedpref.SharedPreferences
import com.example.core.data.source.remote.RemoteDataSource
import com.example.core.data.source.remote.network.ApiService
import com.example.core.domain.repository.IRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val sharedPreferencesModule = module {
    single {
        androidContext().getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
    }
    single { SharedPref(get()) }
    single<ISharedPreferences> { SharedPreferences(get()) }
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
            .baseUrl("https://hrmonitoring-api.herokuapp.com/api/")
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