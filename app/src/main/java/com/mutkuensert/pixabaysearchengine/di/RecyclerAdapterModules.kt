package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.MyDownloader
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.videosscreen.VideosRecyclerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object RecyclerAdapterModules {

    @FragmentScoped
    @Provides
    fun providesImagesRecyclerAdapter(): ImagesRecyclerAdapter{
        return ImagesRecyclerAdapter(MyDownloader())
    }

    @FragmentScoped
    @Provides
    fun providesVideosRecyclerAdapter(): VideosRecyclerAdapter{
        return VideosRecyclerAdapter()
    }
}