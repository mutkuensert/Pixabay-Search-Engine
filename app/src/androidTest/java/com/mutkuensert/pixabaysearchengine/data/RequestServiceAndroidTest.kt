package com.mutkuensert.pixabaysearchengine.data

import com.mutkuensert.pixabaysearchengine.data.source.RequestService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
    fun errorBodyIsNull_responseBodyIsNotNull_httpCodeIs200(){
        assert(response.errorBody() == null)
        assert(response.body() != null)
        assert(response.code() == 200)
    }

}