package com.evya.myweatherapp.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R

class DeleteFavoritesDialog : DialogFragment() {
    companion object {
        const val RESULT = "delete_favorites"
        const val DELETE_ALL = "delete_all"
        const val CITY = "city"
        fun newInstance(all: Boolean, city: String = "") = DeleteFavoritesDialog().apply {
            arguments = bundleOf(DELETE_ALL to all, CITY to city)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val all = requireArguments().getBoolean(DELETE_ALL)
        val city = requireArguments().getString(CITY).orEmpty()
        return AlertDialog.Builder(requireContext())
            .setTitle(if (all) R.string.favorites_clear_all else R.string.favorites_remove_title)
            .setMessage(if (all) getString(R.string.favorites_remove_all_message)
                else getString(R.string.favorites_remove_message, city))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.remove) { _, _ ->
                parentFragmentManager.setFragmentResult(RESULT, bundleOf(DELETE_ALL to all, CITY to city))
            }
            .create()
    }
}
