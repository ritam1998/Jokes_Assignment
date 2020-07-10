package com.example.assignments

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

class ViewModel(application: Application) : AndroidViewModel(application) {

    private var fetchJokes : FetchJokes? = null
    private var allJokesList : ArrayList<JokesModel>? = null
    var livedata : MutableLiveData<ArrayList<JokesModel>>? = null

    lateinit var jokesModel: JokesModel

    init {
        if(livedata == null){
            livedata = MutableLiveData()
        }
    }
    fun getALLJokes(userInput : String?,context: Context){

        viewModelScope.launch(Dispatchers.IO){
            fetchJokes = FetchJokes(context)
            val retrofitData = fetchJokes?.jokesData(userInput)
            allJokesLiveData(retrofitData)
        }
    }

    suspend fun allJokesLiveData(retrofitData: Response<JsonObject>?) {

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                try{
                    if(retrofitData?.isSuccessful!!){
                        val jokes = retrofitData.body().toString()
                        Log.e("Data","$jokes")

                        allJokesList = ArrayList<JokesModel>()

                        val jokesObject = JSONObject(jokes ?:"")
                        val jokesArray = jokesObject.getJSONArray("value")

                        for(i in 0 until jokesArray.length()){
                            val getJokesObject = jokesArray.getJSONObject(i)
                            Log.e("jokes","${getJokesObject?.getString("joke")}")
                            jokesModel = JokesModel(jokes = getJokesObject?.getString("joke"))

                            allJokesList?.add(jokesModel)
                        }
                        livedata?.postValue(allJokesList)
                    }else{
                        Log.e("Error","${retrofitData.message()}")
                    }
                }catch (e : HttpException){
                    Log.e("Exception","$e")
                }
            }
        }
    }
}