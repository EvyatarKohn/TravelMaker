package com.evya.myweatherapp.util

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.evya.myweatherapp.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun forecastTime(epochSeconds: Long, timezone: String, pattern: String = "HH:mm"): String {
    val zone = runCatching { ZoneId.of(timezone) }.getOrDefault(ZoneOffset.UTC)
    // App UI strings are English; don't follow the device language for day/month names.
    return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH).withZone(zone)
        .format(Instant.ofEpochSecond(epochSeconds))
}

/** English relative time for dashboard status (avoids device-locale Hebrew/etc.). */
fun relativeTimeEnglish(thenMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val delta = (nowMillis - thenMillis).coerceAtLeast(0L)
    val minutes = delta / 60_000L
    return when {
        minutes < 1 -> "just now"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        minutes < 120 -> "1 hour ago"
        minutes < 24 * 60 -> "${minutes / 60} hours ago"
        minutes < 48 * 60 -> "1 day ago"
        else -> "${minutes / (24 * 60)} days ago"
    }
}

fun weatherIcon(code: Int?) = when (code) {
    in 200..599 -> R.drawable.ic_rain
    in 600..699 -> R.drawable.ic_snow
    800 -> R.drawable.ic_sun
    else -> R.drawable.ic_sun_cloud
}

/** Centered metric card: muted label on top, bold value below. */
fun dashboardMetric(context: Context, labelAndValue: String): CharSequence {
    val breakAt = labelAndValue.indexOf('\n')
    if (breakAt <= 0 || breakAt >= labelAndValue.lastIndex) return labelAndValue
    val secondary = ContextCompat.getColor(context, R.color.ink_secondary)
    val ink = ContextCompat.getColor(context, R.color.ink)
    return SpannableString(labelAndValue).apply {
        setSpan(AbsoluteSizeSpan(11, true), 0, breakAt, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(ForegroundColorSpan(secondary), 0, breakAt, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(AbsoluteSizeSpan(16, true), breakAt + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(StyleSpan(Typeface.BOLD), breakAt + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(ForegroundColorSpan(ink), breakAt + 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}
