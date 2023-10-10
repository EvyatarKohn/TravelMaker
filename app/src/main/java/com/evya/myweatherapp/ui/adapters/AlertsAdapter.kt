package com.evya.myweatherapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.evya.myweatherapp.databinding.AlertItemBinding
import com.evya.myweatherapp.firebaseanalytics.FireBaseEvents
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsNamesStrings.*
import com.evya.myweatherapp.firebaseanalytics.FireBaseEventsParamsStrings.*
import com.evya.myweatherapp.model.weathermodel.Alerts
import com.evya.myweatherapp.ui.dialogs.AlertsDialog
import java.time.Instant
import java.time.ZoneId

class AlertsAdapter(private val fm: FragmentManager, private val alerts: List<Alerts>): RecyclerView.Adapter<AlertsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        val itemBinding = AlertItemBinding.inflate(LayoutInflater.from(parent.context))

        return AlertsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        holder.bind(fm, alerts[position].event, alerts[position].start, alerts[position].description)
    }

    override fun getItemCount() = alerts.size
}

class AlertsViewHolder(itemBinding: AlertItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    private var title: TextView? = null

    init {
        title = itemBinding.alertTitle
    }

    fun bind(fm: FragmentManager, alertsEvent: String, alertsStart: Int, alertsDescription: String) {
        title?.text = alertsEvent

        itemView.setOnClickListener {

            val params = bundleOf(
                PARAMS_ALERT_DAY.paramsName to getAlertDay(alertsStart),
                PARAMS_ALERT_DESCRIPTION.paramsName to alertsDescription
            )
            FireBaseEvents.sendFireBaseCustomEvents(CHOOSE_CITY_FROM_TOP_ADAPTER.eventName, params)

            AlertsDialog.newInstance(alertsDescription).show(fm, "Alert_DIALOG")

        }
    }

    private fun getAlertDay(alertsStart: Int): String {
        val day = Instant.ofEpochMilli(alertsStart.toLong()).atZone(ZoneId.systemDefault()).dayOfMonth.toString()
        val month = Instant.ofEpochMilli(alertsStart.toLong()).atZone(ZoneId.systemDefault()).monthValue.toString()

        return "$day/$month"

    }
}