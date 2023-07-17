package com.mutkuensert.pixabaysearchengine.libraries.appscope

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class AppScope : DefaultLifecycleObserver, CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cancel()
    }
}