package com.mutkuensert.pixabaysearchengine.libraries.downloader

import android.content.Context
import com.mutkuensert.downloader.Downloader
import com.mutkuensert.pixabaysearchengine.libraries.appscope.AppScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DownloaderModule {

    @Provides
    fun providesDownloader(scope: AppScope, @ApplicationContext context: Context): Downloader {
        return Downloader.Builder()
            .context(context)
            .scope(scope)
            .setNotificationsActive(true)
            .build()
    }
}