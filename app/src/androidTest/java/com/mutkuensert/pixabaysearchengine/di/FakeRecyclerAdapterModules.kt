package com.mutkuensert.pixabaysearchengine.di

import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.FakeImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListenerImplTestVersion
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapter
import com.mutkuensert.pixabaysearchengine.ui.imagesscreen.ImagesRecyclerAdapterClickListener
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
    fun providesFakeRecyclerAdapterClickListener(): ImagesRecyclerAdapterClickListener {
        return ImagesRecyclerAdapterClickListenerImplTestVersion()
    }

    @FragmentScoped
    @Provides
    fun providesFakeImagesRecyclerAdapter(imagesRecyclerAdapterClickListener: ImagesRecyclerAdapterClickListener): ImagesRecyclerAdapter{
        return FakeImagesRecyclerAdapter(imagesRecyclerAdapterClickListener)
    }
}