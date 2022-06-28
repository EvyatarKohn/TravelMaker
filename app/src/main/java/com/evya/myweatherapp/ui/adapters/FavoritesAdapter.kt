package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.Constants.FROM_FAVORITES
import com.evya.myweatherapp.Constants.LAT
import com.evya.myweatherapp.Constants.LONG
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.FavoritesItemLayoutBinding
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.ui.fragments.FavoritesFragment
import com.evya.myweatherapp.util.FireBaseEvents

class FavoritesAdapter(
    private val weather: List<Weather>,
    private val navController: NavController,
    private val favoritesFragment: FavoritesFragment
) : RecyclerView.Adapter<FavoritesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val itemBinding = FavoritesItemLayoutBinding.inflate(LayoutInflater.from(parent.context))

        return FavoritesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(
            weather[position].name,
            weather[position].coord.lat,
            weather[position].coord.lon,
            navController,
            favoritesFragment
        )
    }

    override fun getItemCount() = weather.size
}

class FavoritesViewHolder(itemBinding: FavoritesItemLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var mCityName: TextView? = null

    init {
        mCityName = itemBinding.name
    }

    fun bind(
        cityName: String,
        lat: Double,
        long: Double,
        navController: NavController,
        favoritesFragment: FavoritesFragment
    ) {
        mCityName?.text = cityName.trim()

        itemView.setOnClickListener {
            val bundle = bundleOf(
                LAT to lat.toFloat(),
                LONG to long.toFloat(),
                FROM_FAVORITES to true
            )
            navController.navigate(R.id.action_favoritesFragment_to_cityFragment, bundle)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ChooseCityFromFavorites.toString() + mCityName?.text.toString())
        }

        itemView.setOnLongClickListener {
            favoritesFragment.deleteSpecificCityFromDBPopUp(cityName)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.DeleteCityFromFavorites.toString() + mCityName?.text.toString())
            true
        }
    }
}