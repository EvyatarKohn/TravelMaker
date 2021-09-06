package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesweathermodel.CityData
import com.evya.myweatherapp.ui.MainListener
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import kotlinx.android.synthetic.main.recycler_view_item.view.city_name
import kotlinx.android.synthetic.main.recycler_view_item.view.temp

class CitiesAdapter(
    private var citiesList: List<CityData>,
    private val mainListener: MainListener,
    private val degreeUnits: String
) : RecyclerView.Adapter<CitiesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return CitiesViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CitiesViewHolder, position: Int) {
        var weatherImage = R.drawable.ic_day
        var degreeUnit = "\u2103"
        var windSpeedDgree =" m/s"
        if (degreeUnits == "imperial") {
            degreeUnit = "\u2109"
            windSpeedDgree = " miles/hr"
        }
        when {
            citiesList[position].clouds.today in 1..99 -> {
                weatherImage = R.drawable.ic_cloudy_day
            }
            citiesList[position].clouds.today == 100 -> {
                weatherImage = R.drawable.ic_cloudy
            }
            citiesList[position].clouds.today == 0 -> {
                weatherImage = R.drawable.ic_day
            }
            citiesList[position].rain != null -> {
                weatherImage = R.drawable.ic_rainy
            }
            citiesList[position].snow != null -> {
                weatherImage = R.drawable.ic_snowy
            }
        }
        holder.bind(
            citiesList[position].name,
            citiesList[position].weather[0].description,
            "wind: " + String.format("%.2f", citiesList[position].wind.speed) + windSpeedDgree,
            weatherImage,
            citiesList[position].main.tempMin.toInt().toString() + degreeUnit,
            citiesList[position].main.tempMax.toInt().toString() + degreeUnit,
            mainListener
        )
    }

    override fun getItemCount() = citiesList.size
}

class CitiesViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_item, parent, false)) {
    private var mCityName: TextView? = null
    private var mClearSky: TextView? = null
    private var mWindSpeed: TextView? = null
    private var mWeatherImage: ImageView? = null
    private var mTemp: TextView? = null

    init {
        mCityName = itemView.city_name
        mClearSky = itemView.clear_sky_rec
        mWindSpeed = itemView.adapter_wind_speed
        mWeatherImage = itemView.weather_image
        mTemp = itemView.temp
    }

    fun bind(
        cityName: String,
        clearSky: String,
        windSpeed: String,
        weatherImage: Int,
        tempMin: String,
        tempMax: String,
        mainListener: MainListener
    ) {
        mCityName?.text = cityName
        mTemp?.text = "min $tempMin - max $tempMax"
        mClearSky?.text = clearSky
        mWindSpeed?.text = windSpeed
        mWeatherImage?.setImageResource(weatherImage)
        itemView.setOnClickListener {
            mainListener.showCityWeather(mCityName?.text.toString(), "", "")
        }
    }
}