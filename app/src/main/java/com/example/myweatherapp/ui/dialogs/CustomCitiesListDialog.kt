package com.example.myweatherapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.myweatherapp.R
import com.example.myweatherapp.ui.MainActivity
import kotlin.math.PI
import kotlin.math.sin

class CustomCitiesListDialog: DialogFragment() {

    private lateinit var mDefaultBtn: Button
    private lateinit var mCustomBtn: Button
    private lateinit var mLatWest: TextView
    private lateinit var mLatEast: TextView
    private lateinit var mLongNorth: TextView
    private lateinit var mLongSouth: TextView
    private lateinit var mError: TextView
    private var mBoundaryBox = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.custom_cities_list_dialog_layout, container, false)

        mLatWest = v.findViewById(R.id.latitude_west)
        mLatEast = v.findViewById(R.id.latitude_east)
        mLongNorth = v.findViewById(R.id.latitude_north)
        mLongSouth = v.findViewById(R.id.latitude_south)
        mError = v.findViewById(R.id.custom_cities_list_dialog_error)
        mError.visibility = View.INVISIBLE



        mDefaultBtn = v.findViewById(R.id.default_cities_list_btn)
        mDefaultBtn.setOnClickListener {
            (activity as MainActivity).replaceToCitiesListFragment("")
            dialog?.dismiss()
        }
        mCustomBtn = v.findViewById(R.id.custom_cities_list_dialog)
        mCustomBtn.setOnClickListener {
            if (mLatWest.text.isNullOrEmpty() && mLatEast.text.isNullOrEmpty() &&
                mLongNorth.text.isNullOrEmpty() && mLongSouth.text.isNullOrEmpty()) {
                mError.visibility = View.VISIBLE
            } else {
                mBoundaryBox = mLatWest.text.toString() + "," + mLongSouth.text.toString() + "," +
                        mLatEast.text.toString() + "," + mLongNorth.text.toString() + ",200"
                (activity as MainActivity).replaceToCitiesListFragment(mBoundaryBox)
                dialog?.dismiss()
            }
        }

        return v
    }

}