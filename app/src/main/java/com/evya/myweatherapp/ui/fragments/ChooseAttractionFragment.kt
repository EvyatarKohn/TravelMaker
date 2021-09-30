package com.evya.myweatherapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.evya.myweatherapp.Constants
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.AttractionEnum
import com.evya.myweatherapp.ui.dialogs.NoAttractionFoundDialog
import com.evya.myweatherapp.ui.viewmodels.PlacesViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.choose_attraction_fragment_layout.*

@AndroidEntryPoint
class ChooseAttractionFragment : Fragment() {

    private val mPlacesViewModel: PlacesViewModel by viewModels()
    private var mLat: String = "32.083333"
    private var mLong: String = "34.7999968"
    private lateinit var mNavController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mNavController = Navigation.findNavController(view)
        setOnClickListener()

        val adapter = ArrayAdapter(activity?.applicationContext!!, android.R.layout.select_dialog_item, Constants.replacedList())
        auto_complete_textview.threshold = 1
        auto_complete_textview.setAdapter(adapter)
        auto_complete_textview.setOnItemClickListener { adapterView, view, i, l ->
            whatToDo(auto_complete_textview.text.toString().replace(" ", "_"), R.string.general_error)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.choose_attraction_fragment_layout, container, false)

        mPlacesViewModel.placesRepo.observe(viewLifecycleOwner, { places ->
            val latLong: ArrayList<LatLng> = ArrayList()
            places.features.forEach {
                latLong.add(LatLng(it.geometry.coordinates[0], it.geometry.coordinates[1]))
            }
            if (latLong.size > 0) {
                val bundle = bundleOf("lat" to mLat, "long" to mLong, "places" to places)
                mNavController.navigate(
                    R.id.action_chooseAttractionFragment_to_googleMapsAttractionFragment,
                    bundle
                )
            } else {
                lottie.visibility = View.GONE
                activity?.supportFragmentManager?.let {
                    NoAttractionFoundDialog.newInstance(auto_complete_textview.text.toString()).show(
                        it, " NO_ATTRACTION_DIALOG")
                }
            }
        })
        mPlacesViewModel.placesRepoError.observe(viewLifecycleOwner, {
            showToast(it)
        })

        return v
    }

    private fun setOnClickListener() {
        get_museums_btn.setOnClickListener {
            whatToDo("museums", R.string.museum_request_error)
        }

        get_cinemas_btn.setOnClickListener {
            whatToDo("cinemas", R.string.cinemas_request_error)
        }

        get_adult_btn.setOnClickListener {
            whatToDo("adult", R.string.adults_request_error)
        }

        get_accommodations_btn.setOnClickListener {
            whatToDo("accomodations", R.string.accommodations_request_error)
        }

        get_amusements_btn.setOnClickListener {
            whatToDo("amusements", R.string.amusements_request_error)
        }

        get_sports_btn.setOnClickListener {
            whatToDo("sport", R.string.sports_request_error)
        }

        get_banks_btn.setOnClickListener {
            whatToDo("banks", R.string.banks_request_error)
        }

        get_foods_btn.setOnClickListener {
            whatToDo("foods", R.string.food_request_error)
        }

        get_shops_btn.setOnClickListener {
            whatToDo("shops", R.string.shops_request_error)
        }

        get_transport_btn.setOnClickListener {
            whatToDo("transport", R.string.transports_request_error)
        }

        get_religion_btn.setOnClickListener {
            whatToDo("religion", R.string.religion_request_error)
        }

        get_natural_btn.setOnClickListener {
            whatToDo("natural", R.string.natural_request_error)
        }

        get_historic_btn.setOnClickListener {
            whatToDo("historic", R.string.historic_request_error)
        }

        get_cultural_btn.setOnClickListener {
            whatToDo("cultural", R.string.natural_request_error)
        }

        get_architecture_btn.setOnClickListener {
            whatToDo("architecture", R.string.architecture_request_error)
        }
    }

    private fun showToast(error: Int) {
        Toast.makeText(
            activity?.applicationContext,
            activity?.applicationContext?.resources?.getString(error),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun whatToDo(kind: String, error: Int) {
        main_layout.visibility = View.GONE
        lottie.visibility = View.VISIBLE
        mPlacesViewModel.getWhatToDo(mLong, mLat, kind, error)
    }
}