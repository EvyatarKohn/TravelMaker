package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.Constants.IMPERIAL
import com.evya.myweatherapp.Constants.IMPERIAL_DEGREE
import com.evya.myweatherapp.Constants.METRIC
import com.evya.myweatherapp.Constants.METRIC_DEGREE
import com.evya.myweatherapp.MainData.degreesUnits
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.DailyCityFragmentLayoutBinding
import com.evya.myweatherapp.model.dailyweathermodel.DailyWeather
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DailyDialog: DialogFragment() {

    private var mBinding: DailyCityFragmentLayoutBinding? = null
    private var mWeather: DailyWeather? = null
    private var mCityName: String = ""

    companion object {
        fun newInstance(dailyWeather: DailyWeather?, cityName: String) = DailyDialog().apply {
            mWeather = dailyWeather
            mCityName = cityName
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = DailyCityFragmentLayoutBinding.inflate(layoutInflater)

        setDataToTextViews()

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

    private fun setDataToTextViews() {
        val data = mWeather?.data?.firstOrNull()
        mBinding?.apply {
            cityName.text = mCityName
            temp.text = getDegreeUnits(data?.temp ?: 0.0)
            feelsLike.text = "Feels like: " + getDegreeUnits(data?.feelsLike ?: 0.0)
            dailyExpectation.text = data?.weather?.firstOrNull()?.description
            if (isWinter(data?.temp ?: 15.0)) {
                mainImage.setImageResource(R.drawable.ic_winter)
            } else {
                mainImage.setImageResource(R.drawable.ic_summer)
            }
            val day = Instant.ofEpochMilli(data?.dt?.times(1000L) ?: 0).atZone(ZoneId.systemDefault()).dayOfMonth.toString()
            val month = Instant.ofEpochMilli(data?.dt?.times(1000L) ?: 0).atZone(ZoneId.systemDefault()).monthValue.toString()
            date.text = "Date: $day/$month"
            sunrise.text = "sunrise:\n" + setTimeToHour(data?.sunrise ?: 0)
            sunset.text = "sunset:\n" + setTimeToHour(data?.sunset ?: 0)
            humidity.text = "Humidity:\n" + data?.humidity.toString() + "%"
            windSpeed.text = getWindSpeedDegree(data?.windSpeed)
            description.text = "Weather:\n" + data?.weather?.firstOrNull()?.main
            visibility.text = getVisibilityUnits(data?.visibility ?: 0)
        }
    }

    private fun getDegreeUnits(temp: Double): String {
        return if (degreesUnits == IMPERIAL) {
            temp.toInt().toString() + " \u2109"
        } else {
            temp.toInt().toString() + " \u2103"
        }
    }

    private fun isWinter(temp: Double) = if (degreesUnits == METRIC) {
        temp <= 10
    } else {
        temp <= 50
    }

    private fun setTimeToHour(time: Int): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(time.toLong() * 1000))
    }

    private fun getWindSpeedDegree(windSpeed: Double?): String {
        return "Wind\nspeed:\n" + windSpeed.toString() + if (degreesUnits == IMPERIAL) {
            IMPERIAL_DEGREE
        } else {
            METRIC_DEGREE
        }
    }

    private fun getVisibilityUnits(visibility: Int): String {
        return "Visibility:\n" + if (degreesUnits == IMPERIAL) {
            (visibility / 1609).toString() + Constants.MILE
        } else {
            (visibility / 1000).toString() + Constants.KM
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}