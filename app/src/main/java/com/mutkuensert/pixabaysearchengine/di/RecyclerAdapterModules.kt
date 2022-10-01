package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListener
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListenerImpl
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
        return ImagesRecyclerAdapter(ImagesRecyclerAdapterClickListenerImpl())
    }

    @FragmentScoped
    @Provides
    fun providesVideosRecyclerAdapter(): VideosRecyclerAdapter{
        return VideosRecyclerAdapter()
    }
}