package com.example.assignments

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.xml.datatype.DatatypeConstants.DAYS
import okhttp3.CacheControl
import okhttp3.Interceptor
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS
import javax.xml.datatype.DatatypeConstants.DAYS
import javax.xml.datatype.DatatypeConstants.SECONDS

class FetchJokes(var context: Context) {

    private var BASE_URL = "http://api.icndb.com/jokes/"
    var retrofit = Retrofit.Builder()

    private val TAG = "RetrofitManager"
    private val HEADER_CACHE_CONTROL = "Cache-Control"
    private val HEADER_PRAGMA = "Pragma"

    private var mCache: Cache? = null
    private var mOkHttpClient: OkHttpClient? = null

    private fun fetchAllJokes(): JokesApiCall {

         mOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(provideOfflineCacheInterceptor())
            .addNetworkInterceptor(provideCacheInterceptor())
            .cache(provideCache())
            .build()

        retrofit.baseUrl(BASE_URL)
        retrofit.addConverterFactory(GsonConverterFactory.create(Gson()))
        retrofit.client(mOkHttpClient)

        val checkApi = retrofit.build().create(JokesApiCall::class.java)

        /*CoroutineScope(Dispatchers.IO).launch {
            val callApi = checkApi.getJokesData(userInputInt)
            withContext(Dispatchers.Main){
                try{
                    if(callApi.isSuccessful){
                        val allJokesdata = callApi.body().toString()
                        getAllJokes?.getAllJokesData(allJokesdata)
                    }else{
                        Log.e("Error","${callApi.message()}")
                    }
                }catch (e : HttpException){
                    Log.e("Exception","$e")
                }
            }
        }*/
        /*callApi.enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("Failed", "${t.message}")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (!response.isSuccessful) {
                    Log.e("Load Failed", "${response.message()}")
                }

                val getAllData = response.body()?.toString()
//                Log.e("sucess","${getAllData}")
                getAllJokes?.getAllJokesData(getAllData)
            }
        })*/

        return checkApi
    }

    suspend fun jokesData(userInput: String?): Response<JsonObject> {

        val userInputInt = Integer.parseInt(userInput ?: "")
        return fetchAllJokes().getJokesData(userInputInt)
    }

    fun hasNetwork(context: Context): Boolean? {

        var isConnected: Boolean? = false // Initial Value
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        Log.e("network", "$isConnected")
        return isConnected
    }

    private fun provideCache(): Cache? {
        if (mCache == null) {
            try {
                mCache = Cache(File(context.getCacheDir(), "http-cache"), (10 * 1024 * 1024).toLong()) // 10 MB
            } catch (e: Exception) {
                Log.e(TAG, "Could not create Cache!")
            }
        }
        return mCache
    }

    private fun provideCacheInterceptor(): Interceptor {

        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            val cacheControl: CacheControl = if(hasNetwork(context)!!) {
                CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }

            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }

    private fun provideOfflineCacheInterceptor() : Interceptor{

        return Interceptor { chain ->
            var request = chain.request()

            if (!hasNetwork(context)!!) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }

            chain.proceed(request)
        }
    }
}
