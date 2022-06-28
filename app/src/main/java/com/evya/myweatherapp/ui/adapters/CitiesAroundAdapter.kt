package com.evya.myweatherapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.Constants.CITY_NAME
import com.evya.myweatherapp.Constants.FROM_TOP_ADAPTER
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.MainCitiesItemLayoutBinding
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import com.evya.myweatherapp.util.FireBaseEvents

class CitiesAroundAdapter(
    private val context: Context?,
    private var citiesList: List<CitiesAroundData>,
    private val navController: NavController
) : RecyclerView.Adapter<MainCitiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCitiesViewHolder {
        val itemBinding = MainCitiesItemLayoutBinding.inflate(LayoutInflater.from(parent.context))

        return MainCitiesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MainCitiesViewHolder, position: Int) {
        holder.bind(context, citiesList[position].name, navController)
    }

    override fun getItemCount() = citiesList.size
}

class MainCitiesViewHolder(itemBinding: MainCitiesItemLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var mCityName: TextView? = null

    init {
        mCityName = itemBinding.name
    }

    fun bind(context: Context?, cityName: String, navController: NavController) {
        mCityName?.text = cityName.trim()

        itemView.setOnClickListener {
            val typeFace = context?.let { ResourcesCompat.getFont(it, R.font.product_sans_bold) }
            mCityName?.typeface = typeFace
            val bundle = bundleOf(CITY_NAME to mCityName?.text.toString(), FROM_TOP_ADAPTER to true)
            navController.navigate(R.id.action_cityFragment_self, bundle)
            FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ChooseCityFromTopAdapter.toString() + mCityName?.text.toString())
        }
    }
}