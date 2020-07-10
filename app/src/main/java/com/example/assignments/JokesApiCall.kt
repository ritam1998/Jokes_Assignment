package com.example.assignments

import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JokesApiCall {

    @GET("random/{id}")
     suspend fun getJokesData(@Path("id") id : Int) : Response<JsonObject>
}