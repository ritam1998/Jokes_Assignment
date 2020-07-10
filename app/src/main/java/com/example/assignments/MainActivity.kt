package com.example.assignments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var searchJokesButton : Button? = null
    private var noOfJokesText : EditText? = null
    private var jokesRecyclerView : JokesRecyclerView? = null
    private var recyclerView : RecyclerView? = null

    private var scope : CoroutineScope? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchJokesButton = findViewById(R.id.searchJokesButton)
        noOfJokesText = findViewById(R.id.setNoOfJokes)

        recyclerView = findViewById(R.id.recyclerview)
        scope = CoroutineScope(Dispatchers.IO)

        searchJokesButton?.setOnClickListener {
            searchNoOfJokes(noOfJokesText)
        }
    }

    private fun searchNoOfJokes(noOfJokesString: EditText?) {

        if(noOfJokesString?.text?.trim()?.toString()?.equals("") == true){
            Toast.makeText(this,"Please Fill The Text",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"done",Toast.LENGTH_LONG).show()

            recyclerView?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

            jokesRecyclerView = JokesRecyclerView()
            jokesRecyclerView?.clearJokesData()

            val viewModel = ViewModelProviders.of(this).get(ViewModel :: class.java)
            viewModel.getALLJokes(noOfJokesString?.text?.trim()?.toString(),this)

            viewModel.livedata?.observe(this,
                Observer<ArrayList<JokesModel>> { t -> jokesRecyclerView?.setAllJokes(t) })

            searchJokesButton?.visibility = View.GONE
            noOfJokesString?.visibility = View.GONE

            recyclerView?.adapter = jokesRecyclerView
        }
    }
}
