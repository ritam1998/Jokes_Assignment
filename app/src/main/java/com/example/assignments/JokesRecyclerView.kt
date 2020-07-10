package com.example.assignments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.assignments.databinding.LayoutShowallJokesBinding;


class JokesRecyclerView : RecyclerView.Adapter<JokesRecyclerView.JokesViewHolder>() {

    private var jokesList : ArrayList<JokesModel>?= null

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): JokesRecyclerView.JokesViewHolder {
        val binding = DataBindingUtil.inflate<LayoutShowallJokesBinding>(LayoutInflater.from(parent.context),R.layout.layout_showall_jokes,parent,false)
        return JokesViewHolder(binding)
    }
    override fun getItemCount(): Int {
        Log.e("size","${jokesList?.size}")
        return jokesList?.size ?: 0
    }

    override fun onBindViewHolder(holder: JokesRecyclerView.JokesViewHolder, position: Int) {
        jokesList?.get(position)?.let { holder.jokesViewHolder(it) }
    }
    class JokesViewHolder(var itView: LayoutShowallJokesBinding) : RecyclerView.ViewHolder(itView.root) {

        fun jokesViewHolder(jokesModel: JokesModel){
            itView.setVariable(BR.model,jokesModel)
            itView.executePendingBindings()
        }
    }
    fun setAllJokes(jokesList: ArrayList<JokesModel>?){
        this.jokesList = jokesList
        notifyDataSetChanged()
    }
    fun clearJokesData(){
        jokesList?.clear()
    }
}