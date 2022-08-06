package com.mutkuensert.pixabaysearchengine

import androidx.test.core.app.ApplicationProvider
import com.mutkuensert.pixabaysearchengine.data.ImagesModel
import com.mutkuensert.pixabaysearchengine.data.RequestService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import javax.inject.Inject

@HiltAndroidTest
class RequestServiceAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var requestService: RequestService
    private lateinit var response: Response<ImagesModel>

    @Before
    fun initRequestService(){
        hiltRule.inject()
    }

    @Before
    fun getRequestServiceResponse() = runTest {
        response = requestService.searchImageRequest(search="image")
    }

    @Test
    fun errorBodyIsNull(){
        assert(response.errorBody() == null)
    }

    @Test
    fun responseBodyIsNotNull(){
        assert(response.body() != null)
    }

    @Test
    fun httpCodeIs200(){
        assert(response.code() == 200)
    }

}