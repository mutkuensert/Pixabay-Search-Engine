package com.mutkuensert.pixabaysearchengine.util

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
        //Addition to original after the line
        fun <T> standby(data: T?): Resource<T> {
            return Resource(Status.STANDBY, data, null)
        }

    }

}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
    //Addition to original after the line
    ,STANDBY
}
//Origin of Resource Class: https://blog.mindorks.com/mvvm-architecture-android-tutorial-for-beginners-step-by-step-guide