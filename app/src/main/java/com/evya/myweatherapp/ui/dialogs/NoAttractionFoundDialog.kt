package com.evya.myweatherapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R

class NoAttractionFoundDialog : DialogFragment() {

    private lateinit var mTitle: TextView
    private lateinit var mAttractionName: String

    companion object {
        fun newInstance(attractionName: String) = NoAttractionFoundDialog().apply {
            mAttractionName = attractionName
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.no_attraction_dialog_layout, container, false)

        mTitle = v.findViewById(R.id.title)
        mTitle.text =
            resources.getString(R.string.There_is_no_attraction_in_your_area, mAttractionName)

        return v
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}