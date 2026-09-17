package com.evya.myweatherapp.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.evya.myweatherapp.R
import com.evya.myweatherapp.model.weathermodel.Weather
import com.evya.myweatherapp.util.forecastTime

/** Last successful in-app forecast with timestamp; no background API calls. */
class WeatherWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { manager.updateAppWidget(it, views(context)) }
    }
    companion object {
        fun publish(context: Context, weather: Weather) {
            context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE).edit()
                .putString("city", weather.cityName.ifBlank { weather.timezone.substringAfterLast('/').replace('_', ' ') })
                .putString("temperature", weather.getDegreeUnits(weather.current.temp))
                .putString("description", weather.current.weather.firstOrNull()?.description.orEmpty())
                .putString("updated", forecastTime(weather.callTime / 1000, weather.timezone, "d MMM HH:mm"))
                .apply()
            val manager = AppWidgetManager.getInstance(context)
            manager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java)).forEach {
                manager.updateAppWidget(it, views(context))
            }
        }
        private fun views(context: Context): RemoteViews {
            val prefs = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
            return RemoteViews(context.packageName, R.layout.weather_widget).apply {
                setTextViewText(R.id.widget_city, prefs.getString("city", context.getString(R.string.app_name)))
                setTextViewText(R.id.widget_temperature, prefs.getString("temperature", "—"))
                setTextViewText(R.id.widget_description, prefs.getString("description", context.getString(R.string.widget_open)))
                val updated = prefs.getString("updated", null)
                setTextViewText(R.id.widget_updated, updated?.let { context.getString(R.string.dashboard_updated, it) }
                    ?: context.getString(R.string.widget_open))
                setOnClickPendingIntent(R.id.widget_root, PendingIntent.getActivity(context, 0,
                    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            }
        }
    }
}
