package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.BuildConfig
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.InfoDialogLayoutBinding

class InfoDialog : DialogFragment() {

    private var mBinding: InfoDialogLayoutBinding? = null

    companion object {
        fun newInstance() = InfoDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = InfoDialogLayoutBinding.inflate(LayoutInflater.from(context))

        val italicSpan = SpannableString(mBinding?.privacyPolicyUrl?.text)

        mBinding?.apply {
            version.text = context?.getString(R.string.app_version, BuildConfig.VERSION_NAME)
            privacyPolicyUrl.text?.length?.let {
                italicSpan.setSpan(
                    UnderlineSpan(), 0,
                    it, 0
                )
            }
            privacyPolicyUrl.text = italicSpan
            privacyPolicyUrl.setOnClickListener {
                val privacyPolicyIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(resources.getString(R.string.privacy_policy_url))
                )
                startActivity(privacyPolicyIntent)
            }
        }

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
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