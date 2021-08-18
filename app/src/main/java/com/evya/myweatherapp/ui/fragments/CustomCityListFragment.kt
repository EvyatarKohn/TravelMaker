package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainListener

class CustomCityListFragment: Fragment() {

    private lateinit var mMainListener: MainListener

    companion object {
        fun newInstance(mainListener: MainListener) = CustomCityListFragment().apply {
            mMainListener = mainListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.custom_cities_list_layout, container, false)
    }

      /*  mCityName = v.findViewById(R.id.city_name)
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
    }*/
}