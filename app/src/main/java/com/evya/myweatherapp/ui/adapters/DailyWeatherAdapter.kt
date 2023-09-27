package com.evya.myweatherapp.ui.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.DailyWeatherItemBinding
import com.evya.myweatherapp.model.weathermodel.Daily
import java.time.Instant
import java.time.ZoneId

class DailyWeatherAdapter(
    private var dailyWeatherList: List<Daily>,
    private var minTempArray: List<Int>,
    private var maxTempArray: List<Int>,
    private var context: Context?
) :
    RecyclerView.Adapter<DailyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val itemBinding = DailyWeatherItemBinding.inflate(LayoutInflater.from(parent.context))
        return DailyWeatherViewHolder(itemBinding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        var weatherImage = R.drawable.ic_sun
        when (dailyWeatherList[position].weather[0].description) {
            "clear sky" -> {
                weatherImage = R.drawable.ic_sun
            }
            "few clouds",
            "scattered clouds",
            "overcast clouds",
            "broken clouds" -> {
                weatherImage = R.drawable.ic_sun_cloud
            }
            "rain",
            "light rain" -> {
                weatherImage = R.drawable.ic_rain
            }
            "snow",
            "light snow" -> {
                weatherImage = R.drawable.ic_snow
            }
        }

        holder.bind(
            Instant.ofEpochMilli(dailyWeatherList[position].dt * 1000L).atZone(ZoneId.systemDefault()).dayOfMonth.toString(),
            Instant.ofEpochMilli(dailyWeatherList[position].dt * 1000L).atZone(ZoneId.systemDefault()).monthValue.toString(),
            weatherImage,
            minTempArray[position],
            maxTempArray[position].toString(),
            context
        )
    }

    override fun getItemCount() = dailyWeatherList.size
}

class DailyWeatherViewHolder(itemBinding: DailyWeatherItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var mDate: TextView? = null
    private var mSunImage: ImageView? = null
    private var mTempVar: TextView? = null

    init {
        mDate = itemBinding.date
        mSunImage = itemBinding.sunImage
        mTempVar = itemBinding.tempVar
    }

    fun bind(
        day: String,
        month: String,
        sunImage: Int,
        minTemp: Int,
        maxTemp: String,
        context: Context?
    ) {
        mDate?.text = context?.getString(R.string.date, day, month)
        mSunImage?.setBackgroundResource(sunImage)
        var maxTempForMinus = maxTemp
        if (maxTemp.toInt() < 0) {
            maxTempForMinus = "($maxTemp)"
        }
        mTempVar?.text = context?.getString(R.string.temp_var_2, minTemp, maxTempForMinus)
    }
}