package com.mutkuensert.pixabaysearchengine.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.mutkuensert.pixabaysearchengine.data.service.RequestService
import com.mutkuensert.pixabaysearchengine.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesRequestService(@ApplicationContext context: Context): RequestService {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient
            .Builder()
            .addInterceptor(ChuckerInterceptor(context))
            .build()

        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RequestService::class.java)
    }
}