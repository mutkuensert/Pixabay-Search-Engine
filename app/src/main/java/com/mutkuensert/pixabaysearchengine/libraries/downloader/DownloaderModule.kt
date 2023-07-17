package com.mutkuensert.pixabaysearchengine.libraries.downloader

import com.mutkuensert.pixabaysearchengine.libraries.appscope.AppScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DownloaderModule {

    @Provides
    fun providesDownloader(scope: AppScope) = Downloader(scope)
}