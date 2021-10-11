package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.InfoDialogLayoutBinding
import io.opencensus.resource.Resource

class InfoDialog : DialogFragment() {

    private lateinit var mBinding: InfoDialogLayoutBinding

    companion object {
        fun newInstance() = InfoDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = InfoDialogLayoutBinding.inflate(LayoutInflater.from(context))

        mBinding.privacyPolicyUrl
        val italicSpan = SpannableString(mBinding.privacyPolicyUrl.text)
        italicSpan.setSpan(UnderlineSpan(), 0, mBinding.privacyPolicyUrl.text.length, 0)
        mBinding.privacyPolicyUrl.text = italicSpan

        mBinding.privacyPolicyUrl.setOnClickListener {
            val privacyPolicyIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.privacy_policy_url))
            )
            startActivity(privacyPolicyIntent)
        }

        return AlertDialog.Builder(requireActivity()).setView(mBinding.root).create()

    }

    /*  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
      ): View = inflater.inflate(R.layout.info_dialog_layout, container, false)
  */

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}