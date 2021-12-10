package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.MainCitiesItemLayoutBinding
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.util.FireBaseEvents

class FavoritesAdapter(
    private val citiesName: List<Weather>,
    private val navController: NavController
) : RecyclerView.Adapter<FavoritesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val itemBinding = MainCitiesItemLayoutBinding.inflate(LayoutInflater.from(parent.context))

        return FavoritesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(citiesName[position].name, navController)
    }

    override fun getItemCount() = citiesName.size
}

class FavoritesViewHolder(itemBinding: MainCitiesItemLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var mCityName: TextView? = null

    init {
        mCityName = itemBinding.name
    }

    fun bind(cityName: String, navController: NavController) {
        mCityName?.text = cityName.trim()

        itemView.setOnClickListener {
            val bundle = bundleOf("cityName" to mCityName?.text.toString(), "fromFavorites" to true)
            navController.navigate(R.id.action_favoritesFragment_to_cityFragment, bundle)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ChooseCityFromTopAdapter.toString() + mCityName?.text.toString())
        }
    }
}