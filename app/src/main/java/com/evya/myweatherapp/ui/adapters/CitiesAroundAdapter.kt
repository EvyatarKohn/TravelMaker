package com.evya.myweatherapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.citiesaroundmodel.CitiesAroundData
import kotlinx.android.synthetic.main.main_cities_item_layout.view.*

class CitiesAroundAdapter(
    private val context: Context?,
    private var citiesList: List<CitiesAroundData>,
    private val navController: NavController
) : RecyclerView.Adapter<MainCitiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCitiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return MainCitiesViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MainCitiesViewHolder, position: Int) {
        holder.bind(context, citiesList[position].name, navController)
    }

    override fun getItemCount() = citiesList.size

}

class MainCitiesViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.main_cities_item_layout, parent, false)) {
    private var mCityName: TextView? = null

    init {
        mCityName = itemView.name
    }

    fun bind(context: Context?, cityName: String, navController: NavController) {
        mCityName?.text = cityName.trim()

        itemView.setOnClickListener {
            val typeFace = context?.let { ResourcesCompat.getFont(it, R.font.product_sans_bold) }
            mCityName?.typeface = typeFace
            val bundle = bundleOf("cityName" to mCityName?.text.toString(), "fromTopAdapter" to true)
            navController.navigate(R.id.action_cityFragment_self, bundle)
        }
    }
}