package com.evya.myweatherapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.DailyWeatherItemBinding
import com.evya.myweatherapp.model.weathermodel.Daily
import com.evya.myweatherapp.ui.fragments.CityFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import java.time.ZoneId

@ExperimentalCoroutinesApi
class DailyWeatherAdapter(
    private var cityFragment: CityFragment,
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
            cityFragment,
            Instant.ofEpochMilli(dailyWeatherList[position].dt * 1000L).atZone(ZoneId.systemDefault()).dayOfMonth.toString(),
            Instant.ofEpochMilli(dailyWeatherList[position].dt * 1000L).atZone(ZoneId.systemDefault()).monthValue.toString(),
            dailyWeatherList[position].dt,
            weatherImage,
            minTempArray[position],
            maxTempArray[position].toString(),
            context
        )
    }

    override fun getItemCount() = dailyWeatherList.size
}

@ExperimentalCoroutinesApi
class DailyWeatherViewHolder(itemBinding: DailyWeatherItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var mRoot: ConstraintLayout? = null
    private var mDate: TextView? = null
    private var mSunImage: ImageView? = null
    private var mTempVar: TextView? = null

    init {
        mRoot = itemBinding.root
        mDate = itemBinding.date
        mSunImage = itemBinding.sunImage
        mTempVar = itemBinding.tempVar
    }

    fun bind(
        cityFragment: CityFragment,
        day: String,
        month: String,
        dt: Int,
        sunImage: Int,
        minTemp: Int,
        maxTemp: String,
        context: Context?
    ) {
        mRoot?.setOnClickListener {
            cityFragment.getSpecificDayWeather?.invoke(dt)
        }
        mDate?.text = context?.getString(R.string.date, day, month)
        mSunImage?.setBackgroundResource(sunImage)
        var maxTempForMinus = maxTemp
        if (maxTemp.toInt() < 0) {
            maxTempForMinus = "($maxTemp)"
        }
        mTempVar?.text = context?.getString(R.string.temp_var_2, minTemp, maxTempForMinus)
    }
}