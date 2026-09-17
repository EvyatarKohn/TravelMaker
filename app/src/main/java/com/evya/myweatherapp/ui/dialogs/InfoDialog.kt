package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
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
        mBinding = InfoDialogLayoutBinding.inflate(layoutInflater)

        mBinding?.apply {
            version.text = context?.getString(R.string.app_version, BuildConfig.VERSION_NAME)
        }
        makeUrlClickable(mBinding)

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

    private fun makeUrlClickable(mBinding: InfoDialogLayoutBinding?) {
        mBinding?.apply {
            // icon url
            italicSpan(iconsCreditUrl)
            iconsCreditUrl.setOnClickListener {
                navigateToUrl(R.string.icons_credit_url)
            }

            // icon url 2
            italicSpan(iconsCreditUrl2)
            iconsCreditUrl2.setOnClickListener {
                navigateToUrl(R.string.icons_credit_url2)
            }

            // icon url 3
            italicSpan(iconsCreditUrl3)
            iconsCreditUrl3.setOnClickListener {
                navigateToUrl(R.string.icons_credit_url3)
            }

            // lottie
            italicSpan(lottieCreditUrl)
            lottieCreditUrl.setOnClickListener {
                navigateToUrl(R.string.lottie_credit_url)

            }

            italicSpan(privacyPolicyUrl)
            privacyPolicyUrl.setOnClickListener {
                navigateToUrl(R.string.privacy_policy_url)
            }
        }
    }

    private fun italicSpan(textView: TextView) {
        val span = SpannableString(textView.text)
        span.setSpan(UnderlineSpan(), 0, textView.text.length, 0)
        textView.text = span
    }

    private fun navigateToUrl(urlStringValue: Int) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(urlStringValue))))
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
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