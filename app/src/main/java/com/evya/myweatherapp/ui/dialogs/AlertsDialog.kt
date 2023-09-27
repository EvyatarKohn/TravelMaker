package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.databinding.AlertDialogBinding
import com.evya.myweatherapp.ui.adapters.AlertsAdapter

class AlertsDialog : DialogFragment() {

    private var mBinding: AlertDialogBinding? = null
    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var description: String

    companion object {
        fun newInstance(alertsDescription: String) = AlertsDialog().apply {
            description = alertsDescription
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = AlertDialogBinding.inflate(layoutInflater)

        mBinding?.alertDescription?.text = description

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
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