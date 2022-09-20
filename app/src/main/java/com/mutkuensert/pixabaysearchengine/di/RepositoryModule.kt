package com.mutkuensert.pixabaysearchengine.di

import android.app.Application
import com.mutkuensert.pixabaysearchengine.data.source.ImagesRepository
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
    fun providesImagesRepository(app: Application): ImagesRepository{
        return ImagesRepository(app)
    }
}