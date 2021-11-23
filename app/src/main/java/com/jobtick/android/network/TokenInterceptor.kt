package com.jobtick.android.network

import com.jobtick.android.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor (private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()

        val newUrlBuilder = url.newBuilder()
       // newUrlBuilder.addQueryParameter("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MGUyZWE0YmY0N2Y4YTQ5YzI4YmMxMDYiLCJtb2JpbGVOdW1iZXIiOiIwOTE4NjM3NDIwMSIsImlzcyI6InRyYWRlcnNwbHVzIiwiZXhwIjoxMjAwMDAwMDAwMDAwLCJpYXQiOjE2MjU0ODM4Njh9.xzrHofzCzAYxjKuRN9oJENWsMvdrhqCEzCfxrVCe5Q4")
        val newRequestBuilder = request.newBuilder()
        newRequestBuilder.url(newUrlBuilder.build()).addHeader("Authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken())
        return chain.proceed(newRequestBuilder.build())

    }
}