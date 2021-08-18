package com.evya.myweatherapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainActivity


class PermissionDeniedDialog: DialogFragment() {

    private lateinit var mExitBtn: Button
    private lateinit var mPermissionsBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.permission_denied_dialog_layout, container, false)

        mExitBtn = v.findViewById(R.id.exit_btn)
        mExitBtn.setOnClickListener {
            activity?.finish()
        }
        mPermissionsBtn = v.findViewById(R.id.approve_btn)
        mPermissionsBtn.setOnClickListener {
            (activity as MainActivity).goToPermissionSettings()
            dialog?.dismiss()
        }
        return v
    }
}