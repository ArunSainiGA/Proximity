package com.asp.proximitylabs.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import kotlin.collections.HashMap

data class AirQualityModel (
    val city: String,
    var aqi: String,
    var updated: Long = Calendar.getInstance().timeInMillis
): Parcelable{
    private var history = mutableMapOf<Long, String>()

    fun updateAQI(aqi: String){
        // Updating history
        updateHistory(this.updated, this.aqi)

        this.aqi = aqi
        this.updated = Calendar.getInstance().timeInMillis

        // Updating latest value
        updateHistory(this.updated, this.aqi)
    }

    fun getHistory(): Map<Long, String>{
        return history
    }

    fun setHistory(history: HashMap<Long, String>){
        this.history = history
    }

    private fun updateHistory(time: Long, aqi: String){
        history[time] = aqi
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(aqi)
        parcel.writeLong(updated)
        parcel.writeInt(history.size)
        history.entries.forEach {
            parcel.writeLong(it.key)
            parcel.writeString(it.value)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AirQualityModel> {
        override fun createFromParcel(parcel: Parcel): AirQualityModel {
            val city = parcel.readString()!!
            val aqi = parcel.readString()!!
            val updated = parcel.readLong()
            var history = mutableMapOf<Long, String>()
            for(i in 0 until parcel.readInt()){
                history[parcel.readLong()] = parcel.readString()!!
            }
            val model = AirQualityModel(city, aqi, updated)
            model.history = history
            return model
        }

        override fun newArray(size: Int): Array<AirQualityModel?> {
            return arrayOfNulls(size)
        }
    }
}