package com.evya.myweatherapp.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.evya.myweatherapp.R
import com.evya.myweatherapp.databinding.DeleteFavoritesDilaogLayoutBinding
import com.evya.myweatherapp.ui.fragments.FavoritesFragment

class DeleteFavoritesDialog : DialogFragment() {
    private var mBinding: DeleteFavoritesDilaogLayoutBinding? = null
    private lateinit var mFavoritesFragment: FavoritesFragment
    private var mDeleteAllFavorites: Boolean = false
    private lateinit var mCityName: String

    companion object {
        fun newInstance(
            favoritesFragment: FavoritesFragment,
            deleteAllFavorites: Boolean,
            cityName: String
        ) =
            DeleteFavoritesDialog().apply {
                mFavoritesFragment = favoritesFragment
                mDeleteAllFavorites = deleteAllFavorites
                mCityName = cityName
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mBinding = DeleteFavoritesDilaogLayoutBinding.inflate(LayoutInflater.from(context))

        if (mDeleteAllFavorites) {
            mBinding?.title?.text = resources.getString(R.string.are_you_sure_you_want_to_delete_all)
        } else {
            mBinding?.title?.text = resources.getString(R.string.are_you_sure_you_want_to_delete, mCityName)
        }

        mBinding?.yes?.setOnClickListener {
            if (mDeleteAllFavorites) {
                mFavoritesFragment.deleteAllCitiesFromDB()
            } else {
                mFavoritesFragment.deleteSpecificCityFromDB()
            }
            dismiss()
        }

        mBinding?.no?.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireActivity()).setView(mBinding?.root).create()
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}