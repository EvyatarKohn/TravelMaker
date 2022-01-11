package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.PermissionDeniedDialogLayoutBinding
import com.evya.myweatherapp.ui.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class PermissionDeniedDialog : DialogFragment() {

    private var mBinding: PermissionDeniedDialogLayoutBinding? = null

    private var mIsGpsEnabled = true

    companion object {
        fun newInstance(isGpsEnabled: Boolean) = PermissionDeniedDialog().apply {
            mIsGpsEnabled = isGpsEnabled
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        mBinding = PermissionDeniedDialogLayoutBinding.inflate(LayoutInflater.from(context))
        mBinding?.exitBtn?.setOnClickListener {
            activity?.finish()
        }
        if (mIsGpsEnabled) {
            mBinding?.approveBtn?.text = resources.getString(R.string.approve_permissions)
        }
        mBinding?.approveBtn?.setOnClickListener {
            if (mIsGpsEnabled) {
                (activity as MainActivity).goToPermissionSettings()
            } else {
                (activity as MainActivity).turnGPSOn()
            }
            dismiss()
        }

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}