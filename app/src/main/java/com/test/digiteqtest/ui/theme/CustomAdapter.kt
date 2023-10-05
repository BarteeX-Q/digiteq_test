package com.test.digiteqtest.ui.theme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.digiteqtest.R

class CustomAdapter(private var dataSet: Array<String>, private val context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    fun updateData(dataSet: Array<String>) {
        this.dataSet = dataSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.text_item, parent, false)
        view.animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.getView()
        view.text = dataSet[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.textView.text = dataSet[position]
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        viewToAnimate.startAnimation(animation)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val textView: TextView

        init {
            textView = view.findViewById(R.id.text_view_item)
        }

        fun getView(): TextView = textView
    }
}