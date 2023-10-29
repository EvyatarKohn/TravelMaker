package com.evya.myweatherapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.AlertItemBinding
import com.evya.myweatherapp.model.weathermodel.Alerts
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsAdapter(private val context: Context, private val alerts: List<Alerts>?): RecyclerView.Adapter<AlertsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val itemBinding = AlertItemBinding.inflate(LayoutInflater.from(parent.context))

        return AlertsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        holder.bind(
            context,
            alerts?.get(position)?.event,
            alerts?.get(position)?.start,
            alerts?.get(position)?.end,
            alerts?.get(position)?.description
        )
    }

    override fun getItemCount() = alerts?.size ?: 0
}

class AlertsViewHolder(itemBinding: AlertItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var title: TextView? = null
    private var desc: TextView? = null

    init {
        title = itemBinding.alertTitle
        desc = itemBinding.alertDesc
    }

    fun bind(
        context: Context,
        alertsEvent: String?,
        alertsStart: Int?,
        alertsEnd: Int?,
        alertsDescription: String?
    ) {
        title?.text = context.resources.getString(R.string.alerts_item, alertsEvent, getAlertTime(alertsStart), getAlertTime(alertsEnd))
        desc?.text = alertsDescription
    }
    private fun getAlertTime(alertTime: Int?) = SimpleDateFormat("dd/MM  HH:mm", Locale.getDefault()).format((alertTime ?: 1) * 1000L)
}