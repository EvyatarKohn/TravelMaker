package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.databinding.DailyWeatherItemBinding
import com.evya.myweatherapp.model.weathermodel.Daily
import com.evya.myweatherapp.util.forecastTime
import com.evya.myweatherapp.util.weatherIcon
import kotlin.math.roundToInt

class DailyWeatherAdapter(
    private val days: List<Daily>,
    private val timezone: String,
    private val onDayClick: (Int) -> Unit,
) : RecyclerView.Adapter<DailyWeatherAdapter.Holder>() {
    class Holder(val binding: DailyWeatherItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount() = days.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        DailyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val day = days[position]
        with(holder.binding) {
            date.text = forecastTime(day.dt.toLong(), timezone, "EEE d MMM")
            sunImage.setImageResource(weatherIcon(day.weather.firstOrNull()?.id))
            tempVar.text = "${day.temp.max.roundToInt()}° / ${day.temp.min.roundToInt()}°"
            root.setOnClickListener { onDayClick(day.dt) }
            root.contentDescription = listOf(date.text, day.weather.firstOrNull()?.description.orEmpty(), tempVar.text).joinToString(", ")
        }
    }
}
