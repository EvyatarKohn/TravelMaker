package com.evya.myweatherapp.util

import android.os.Parcelable
import com.evya.myweatherapp.model.placesmodel.Places
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AttractionFragmentUtil(val places: Places): Parcelable