package com.lanhee.mapdiary

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitModule {
    private val BASE_URL = "https://naveropenapi.apigw.ntruss.com/"
    private val client = OkHttpClient.Builder().addNetworkInterceptor(object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val newRequestBuilder = chain
                .request()
                .newBuilder()
                .addHeader("X-NCP-APIGW-API-KEY-ID",MyApplication.CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", MyApplication.CLIENT_SECRET)
            val request = newRequestBuilder.build()
            val response = chain.proceed(request);
            Log.i("TEST", "url > ${request.url()}")
            Log.i("TEST", "response > ${response.code()}")
            Log.i("TEST", "result > ${response.message()}")
            return response
        }
    }).build()

    fun createRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}