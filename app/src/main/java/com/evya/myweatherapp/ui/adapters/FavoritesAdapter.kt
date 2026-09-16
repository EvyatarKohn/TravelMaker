package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoritesItemLayoutBinding
import com.evya.myweatherapp.model.weathermodel.Weather

class FavoritesAdapter(
    private val onOpen: (Weather) -> Unit,
    private val onRemove: (Weather) -> Unit
) : ListAdapter<Weather, FavoritesAdapter.ViewHolder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        FavoritesItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: FavoritesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: Weather) {
            binding.name.text = weather.cityName.trim()
            binding.removeFavorite.contentDescription = binding.root.context.getString(
                R.string.favorites_remove_city, weather.cityName
            )
            binding.root.setOnClickListener { onOpen(weather) }
            binding.removeFavorite.setOnClickListener { onRemove(weather) }
            binding.root.setOnLongClickListener { onRemove(weather); true }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Weather>() {
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Weather, newItem: Weather) = oldItem == newItem
    }
}
