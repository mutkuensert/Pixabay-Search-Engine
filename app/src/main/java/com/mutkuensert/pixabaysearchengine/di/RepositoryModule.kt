package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.data.source.Repository
import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesRepository(requestService: RequestService): Repository{
        return Repository(requestService)
    }
}