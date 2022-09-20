package com.mutkuensert.pixabaysearchengine

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.core.util.Preconditions
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

//https://developer.android.com/training/dependency-injection/hilt-testing#launchfragment
//https://github.com/android/architecture-samples/blob/views-hilt/app/src/androidTest/java/com/example/android/architecture/blueprints/todoapp/HiltExt.kt
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.Theme_PixabaySearchEngine,
    crossinline action: Fragment.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        fragment.action()
    }
}