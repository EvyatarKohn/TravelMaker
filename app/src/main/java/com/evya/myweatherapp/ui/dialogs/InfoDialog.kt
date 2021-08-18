package com.evya.myweatherapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import kotlinx.android.synthetic.main.info_dialog_layout.*

class InfoDialog: DialogFragment() {

    private lateinit var mCloseDialog: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.info_dialog_layout, container, false)

        mCloseDialog = v.findViewById(R.id.exit_btn)
        mCloseDialog.setOnClickListener {
            dismiss()
        }

        return v
    }
}