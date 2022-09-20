package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.data.source.FakeImagesRepository
import com.mutkuensert.pixabaysearchengine.data.source.ImagesRepository
import dagger.Binds
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
    fun providesImagesRepository(): ImagesRepository {
        return FakeImagesRepository()
    }
}