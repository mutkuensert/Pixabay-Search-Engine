package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.*
import com.mutkuensert.pixabaysearchengine.ui.videosscreen.VideosRecyclerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [FragmentComponent::class],
    replaces = [RecyclerAdapterModules::class]
)
object FakeRecyclerAdapterModules {

    @FragmentScoped
    @Provides
    fun providesFakeImagesRecyclerAdapter(): ImagesRecyclerAdapter{
        return FakeImagesRecyclerAdapter()
    }

    @FragmentScoped
    @Provides
    fun providesVideosRecyclerAdapter(): VideosRecyclerAdapter {
        return VideosRecyclerAdapter()
    }
}