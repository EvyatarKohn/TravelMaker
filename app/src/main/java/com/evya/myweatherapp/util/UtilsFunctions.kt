package com.evya.myweatherapp.util

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.evya.myweatherapp.R

class UtilsFunctions {

    companion object {

        fun showToast(error: Int, context: Context?) {
            Toast.makeText(
                context,
                context?.resources?.getString(error),
                Toast.LENGTH_LONG
            ).show()
        }

        fun setSpanBold(start: Int, end: Int, textView: TextView, context: Context?) {
            val span = SpannableString(textView.text.toString())
            val font = Typeface.createFromAsset(context?.assets, "font/product_sans_bold.ttf")
            span.setSpan(CustomTypeFaceSpan("", font), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            textView.text = span
        }


        fun setColorSpan(start: Int, end: Int, color: Int, text: Int, view: TextView, context: Context?) {
            val span = SpannableString(context?.resources?.getString(text))
            span.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context!!,
                        color
                    )
                ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = span
        }
    }
}