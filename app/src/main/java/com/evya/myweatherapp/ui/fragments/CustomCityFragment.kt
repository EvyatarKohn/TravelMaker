package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener

class CustomCityFragment : Fragment() {
    private lateinit var mCityName: TextView
    private lateinit var mLat: TextView
    private lateinit var mLong: TextView
    private lateinit var mMainListener: MainListener
    private lateinit var mBtn: Button
    private lateinit var mToCitiesListBtn: Button
    private lateinit var mErrorMessage: TextView

    companion object {
        fun newInstance(mainListener: MainListener) = CustomCityFragment().apply {
            mMainListener = mainListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.custom_city_fragment_layout, container, false)

        mCityName = v.findViewById(R.id.city_name)
        mLat = v.findViewById(R.id.latitude)
        mLong = v.findViewById(R.id.longitude)
        mBtn = v.findViewById(R.id.continue_btn)
        mToCitiesListBtn = v.findViewById(R.id.to_cities_list_btn)
        mErrorMessage = v.findViewById(R.id.error_title)

        mBtn.setOnClickListener {
            if (mCityName.text.isNullOrEmpty() && (mLat.text.isNullOrEmpty() || mLong.text.isNullOrEmpty())) {
                showErrorMessage(true)
            } else if (!mCityName.text.isNullOrEmpty() && (!mLat.text.isNullOrEmpty() || !mLong.text.isNullOrEmpty())) {
                showErrorMessage(false)
            } else if (mCityName.text.isNullOrEmpty()) {
                mMainListener.replaceFragment("", mLat.text.toString(), mLong.text.toString())
            } else if (mLat.text.isNullOrEmpty() && mLong.text.isNullOrEmpty()) {
                mMainListener.replaceFragment(mCityName.text.toString(), "", "")
            }
        }

        mToCitiesListBtn.setOnClickListener {
            mMainListener.replaceToCitiesListFragment("")
        }

        return v
    }

    private fun showErrorMessage(isAllFieldsEmpty: Boolean) {
        mErrorMessage.visibility = View.VISIBLE
        if (!isAllFieldsEmpty) {
            mErrorMessage.text = resources.getString(R.string.choose_city_error_message_only_one_fields)
        } else {
            mErrorMessage.text = resources.getString(R.string.choose_city_error_message_no_fields)
        }
    }
}

