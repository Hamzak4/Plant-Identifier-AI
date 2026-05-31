package com.example.di

import android.content.Context
import com.example.data.local.PlantDatabase
import com.example.data.remote.GeminiApiService
import com.example.data.repository.PlantRepository
import com.example.data.repository.PlantRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthRepositoryImpl

object ServiceLocator {
    private var database: PlantDatabase? = null
    private var apiService: GeminiApiService? = null
    private var repository: PlantRepository? = null
    private var authRepository: AuthRepository? = null

    private fun getDatabase(context: Context): PlantDatabase {
        return database ?: synchronized(this) {
            val db = PlantDatabase.getDatabase(context)
            database = db
            db
        }
    }

    fun getApiService(): GeminiApiService {
        return apiService ?: synchronized(this) {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val service = retrofit.create(GeminiApiService::class.java)
            apiService = service
            service
        }
    }

    fun getRepository(context: Context): PlantRepository {
        return repository ?: synchronized(this) {
            val repo = PlantRepositoryImpl(
                plantDao = getDatabase(context).plantDao(),
                apiService = getApiService()
            )
            repository = repo
            repo
        }
    }

    fun getAuthRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            val repo = AuthRepositoryImpl(context.applicationContext)
            authRepository = repo
            repo
        }
    }
}
