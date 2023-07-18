package com.mutkuensert.pixabaysearchengine.data.di

import com.mutkuensert.pixabaysearchengine.data.model.repository.RepositoryImpl
import com.mutkuensert.pixabaysearchengine.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsRepository(repositoryImpl: RepositoryImpl): Repository
}