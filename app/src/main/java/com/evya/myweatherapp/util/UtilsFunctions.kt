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

class UtilsFunctions {

    companion object {
        fun showToast(error: String?, context: Context?) {
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_SHORT
            ).show()
        }

        fun setSpanBold(start: Int, end: Int, textView: TextView, context: Context?) {
            val span = SpannableString(textView.text.toString())
            val font = Typeface.createFromAsset(context?.assets, "font/product_sans_bold.ttf")
            span.setSpan(CustomTypeFaceSpan("", font), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            textView.text = span
        }

        fun setSpanBold(string: String, context: Context?): SpannableString {
            val span = SpannableString(string)
            val font = Typeface.createFromAsset(context?.assets, "font/product_sans_bold.ttf")
            span.setSpan(CustomTypeFaceSpan("", font), 0, string.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
            return span
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

        fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
            return if (p1 != null && p2 != null) block(p1, p2) else null
        }
    }
}