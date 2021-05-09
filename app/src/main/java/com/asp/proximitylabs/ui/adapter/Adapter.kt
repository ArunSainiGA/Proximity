package com.asp.proximitylabs.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.asp.proximitylabs.model.AirQualityModel
import com.asp.proximitylabs.R
import com.asp.proximitylabs.utils.TimeUtils
import com.asp.proximitylabs.databinding.ViewItemAirQualityBinding
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class Adapter(private var content: ArrayList<AirQualityModel>, private val listener: (Int, AirQualityModel) -> Unit): RecyclerView.Adapter<Adapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.view_item_air_quality, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.state.text = content[position].city
        holder.index.setTextColor(Color.parseColor(getIndexColor(content[position].aqi)))
        holder.index.text = getIndex(content[position].aqi)
        holder.updated.text = TimeUtils.getPrettyTime(content[position].updated)

        holder.itemView.setOnClickListener {
            listener(position, content[position])
        }
    }

    override fun getItemCount(): Int {
        return content.size
    }

    fun updateContent(content: ArrayList<AirQualityModel>?){
        val itemsToBeAdded = ArrayList<AirQualityModel>()
        content?.let {
            for(i in 0 until it.size) {
                var found = false
                for (j in 0 until this.content.size) {
                    if (it[i].city == this.content[j].city) {
                        found = true
                        this.content[j].updateAQI(it[i].aqi)
                    }
                }
                if (!found)
                    itemsToBeAdded.add(it[i])
            }
        }
        this.content.addAll(itemsToBeAdded)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ViewItemAirQualityBinding): RecyclerView.ViewHolder(binding.root) {
        var state: TextView = binding.state
        var index: TextView = binding.index
        var updated: TextView = binding.updated
    }

    private fun getIndex(str: String): String{
        return String.format("%.2f", str.toDouble())
    }

    private fun getIndexColor(index: String): String{
        return try {
            when(index.toDouble().roundToInt()){
                in 1..50 -> "#55A84F"
                in 51..100 -> "#A3C853"
                in 101..200 -> "#FFF833"
                in 201..300 -> "#F29C33"
                in 301..400 -> "#E93F33"
                in 401..500 -> "#AF2D24"
                else -> "#000000"
            }
        }catch (ex: NumberFormatException){
            "#000000"
        }
    }
}