package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.NoAttractionDialogLayoutBinding
import com.evya.myweatherapp.ui.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class NoAttractionFoundDialog : DialogFragment() {

    private var mBinding: NoAttractionDialogLayoutBinding? = null

    private lateinit var mAttractionName: String

    companion object {
        fun newInstance(attractionName: String) = NoAttractionFoundDialog().apply {
            mAttractionName = attractionName
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        mBinding = NoAttractionDialogLayoutBinding.inflate(LayoutInflater.from(context))
        mBinding?.title?.text =
            resources.getString(R.string.There_is_no_attraction_in_your_area, mAttractionName)

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
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as MainActivity).changeNavBarIndex(
            R.id.chooseAttractionFragment,
            R.id.attractions
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}