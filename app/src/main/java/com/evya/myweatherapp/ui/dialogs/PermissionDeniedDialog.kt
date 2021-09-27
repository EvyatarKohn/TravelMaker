package com.evya.myweatherapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.ui.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class PermissionDeniedDialog: DialogFragment() {

    private lateinit var mTitle: TextView
    private lateinit var mExitBtn: TextView
    private lateinit var mPermissionsBtn: Button
    private var mIsGpsEnabled = true

    companion object {
        fun newInstance(isGpsEnabled: Boolean) = PermissionDeniedDialog().apply {
            mIsGpsEnabled = isGpsEnabled
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.permission_denied_dialog_layout, container, false)
        dialog?.setCanceledOnTouchOutside(false)
        mTitle = v.findViewById(R.id.title)
        mExitBtn = v.findViewById(R.id.exit_btn)
        mExitBtn.setOnClickListener {
            activity?.finish()
        }
        mPermissionsBtn = v.findViewById(R.id.approve_btn)

        if (mIsGpsEnabled) {
            mPermissionsBtn.text = resources.getString(R.string.approve_permissions)
        }
        mPermissionsBtn.setOnClickListener {
            dialog?.dismiss()
            if (mIsGpsEnabled) {
                (activity as MainActivity).goToPermissionSettings()
            } else {
                (activity as MainActivity).turnGPSOn()
            }
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}