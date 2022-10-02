package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.data.source.FakeRepository
import com.mutkuensert.pixabaysearchengine.data.source.Repository
import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object FakeRepositoryModule {

    @Singleton
    @Provides
    fun providesRepository(requestService: RequestService): Repository {
        return FakeRepository(requestService)
    }
}