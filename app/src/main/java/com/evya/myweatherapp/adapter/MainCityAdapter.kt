package com.evya.myweatherapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener
import kotlinx.android.synthetic.main.main_cities_item_layout.view.*


class MainCityAdapter(private val context: Context?, private var citiesList: List<String>, private val mainListener: MainListener) : RecyclerView.Adapter<MainCitiesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCitiesViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return MainCitiesViewHolder(inflater, parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainCitiesViewHolder, position: Int) {
       holder.bind(context, citiesList[position], mainListener)
    }

    override fun getItemCount() = citiesList.size
}

class MainCitiesViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.main_cities_item_layout, parent, false)) {
    private var mCityName: TextView? = null

    init {
        mCityName = itemView.name
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(context: Context?, cityName: String, mainListener: MainListener) {
        mCityName?.text = cityName
        itemView.setOnClickListener {
            val typeFace = ResourcesCompat.getFont(context!!, R.font.product_sans_bold)
            mCityName?.typeface = typeFace
            mainListener.replaceFragment(mCityName?.text.toString(), "", "")
        }
    }
}
