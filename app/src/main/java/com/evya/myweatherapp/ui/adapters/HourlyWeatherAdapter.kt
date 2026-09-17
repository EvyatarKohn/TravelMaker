package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.HourlyWeatherItemBinding
import com.evya.myweatherapp.model.weathermodel.Hourly
import com.evya.myweatherapp.util.forecastTime
import com.evya.myweatherapp.util.weatherIcon
import kotlin.math.roundToInt

class HourlyWeatherAdapter(private val hours: List<Hourly>, private val timezone: String) :
    RecyclerView.Adapter<HourlyWeatherAdapter.Holder>() {
    class Holder(val binding: HourlyWeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount() = hours.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        HourlyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val hour = hours[position]
        with(holder.binding) {
            this.hour.text = forecastTime(hour.dt.toLong(), timezone)
            temperature.text = "${hour.temp.roundToInt()}°"
            icon.setImageResource(weatherIcon(hour.weather.firstOrNull()?.id))
            rain.text = root.context.getString(R.string.hour_rain, (hour.pop.coerceIn(0.0, 1.0) * 100).roundToInt())
            root.contentDescription = listOf(this.hour.text, temperature.text,
                hour.weather.firstOrNull()?.description.orEmpty(), rain.text).joinToString(", ")
            root.importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
    }
}
