package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListener
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListenerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecyclerAdapterModules {

    @Singleton
    @Provides
    fun providesImagesRecyclerAdapterClickListener(): ImagesRecyclerAdapterClickListener{
        return ImagesRecyclerAdapterClickListenerImpl()
    }

    @Singleton
    @Provides
    fun providesImagesRecyclerAdapter(impl: ImagesRecyclerAdapterClickListener): ImagesRecyclerAdapter{
        return ImagesRecyclerAdapter(impl)
    }
}