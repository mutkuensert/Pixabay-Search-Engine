package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import com.mutkuensert.pixabaysearchengine.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun providesRequestService(): RequestService {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RequestService::class.java)
    }
}