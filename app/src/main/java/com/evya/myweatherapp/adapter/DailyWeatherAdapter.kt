package com.evya.myweatherapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeatherData
import kotlinx.android.synthetic.main.daily_weather_item.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DailyWeatherAdapter(
    private var dailyWeatherList: List<DailyWeatherData>,
    private var minTempArray: List<Int>,
    private var maxTempArray: List<Int>
) :
    RecyclerView.Adapter<DailyWeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return DailyWeatherViewHolder(inflater, parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        var sunImage = R.drawable.ic_sun
        when {
            dailyWeatherList[position].weather[0].description.equals("clear sky") -> {
                sunImage = R.drawable.ic_sun
            }
            dailyWeatherList[position].weather[0].description.equals("few clouds") ||
                    dailyWeatherList[position].weather[0].description.equals("scattered clouds") -> {
                sunImage = R.drawable.ic_sun_cloud
            }
        }

        holder.bind(
            LocalDate.parse(dailyWeatherList[position].dtTxt.substring(0, 10), DateTimeFormatter.ISO_DATE).dayOfMonth.toString(),
            LocalDate.parse(dailyWeatherList[position].dtTxt.substring(0, 10), DateTimeFormatter.ISO_DATE).monthValue.toString(),
            sunImage,
            minTempArray[position],
            maxTempArray[position]
        )
    }


    override fun getItemCount() = dailyWeatherList.size
}

class DailyWeatherViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.daily_weather_item, parent, false)) {
    private var mDate: TextView? = null
    private var mSunImage: ImageView? = null
    private var mTempVar: TextView? = null

    init {
        mDate = itemView.date
        mSunImage = itemView.sun_image
        mTempVar = itemView.temp_var
    }

    fun bind(day: String, month: String,  sunImage: Int, minTemp: Int?, maxTemp: Int?) {
        mDate?.text = "$day/$month"
        mSunImage?.setBackgroundResource(sunImage)
        mTempVar?.text = "$minTemp - $maxTemp"
    }
}