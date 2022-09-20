package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.FakeImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListenerImplTestVersion
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RecyclerAdapterModules::class]
)
object FakeRecyclerAdapterModules {

    @Singleton
    @Provides
    fun providesFakeRecyclerAdapterClickListenerImpl(): ImagesRecyclerAdapterClickListenerImplTestVersion{
        return ImagesRecyclerAdapterClickListenerImplTestVersion()
    }

    @Singleton
    @Provides
    fun providesFakeImagesRecyclerAdapter(imagesRecyclerAdapterClickListenerImplTestVersion: ImagesRecyclerAdapterClickListenerImplTestVersion): ImagesRecyclerAdapter{
        return FakeImagesRecyclerAdapter(imagesRecyclerAdapterClickListenerImplTestVersion)
    }
}