package com.mutkuensert.pixabaysearchengine.libraries.appscope

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppScopeModule {

    @Singleton
    @Provides
    fun providesAppScope() = AppScope()
}