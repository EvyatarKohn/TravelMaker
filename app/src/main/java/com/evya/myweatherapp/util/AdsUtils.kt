package com.evya.myweatherapp.util

import android.app.Activity
import android.util.Log
import com.evya.myweatherapp.Constants.GOOGLE_ADS_RELEASE
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdsUtils {
    companion object {

        private var mInterstitialAd: InterstitialAd? = null
        private lateinit var mAdRequest: AdRequest

        fun getAdRequest() =  AdRequest.Builder().build()

        fun handleAds(activity: Activity, tag: String) {
            mAdRequest =  getAdRequest()
            InterstitialAd.load(
                activity,
                GOOGLE_ADS_RELEASE,
                mAdRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(tag, adError.toString())
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d(tag, "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                    }
                })

            if (mInterstitialAd != null) {
                activity.let { mInterstitialAd?.show(it) }
            } else {
                Log.d(tag, "The interstitial ad wasn't ready yet.")
            }

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(tag, "Ad was clicked.")
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ClickOnAd)
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(tag, "Ad dismissed fullscreen content.")
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.CloseAd)
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    Log.e(tag, "Ad failed to show fullscreen content.")
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.FailedToLoadAd)
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(tag, "Ad recorded an impression.")
                    FireBaseEvents.sendFireBaseCustomEvents(FireBaseEvents.FirebaseEventsStrings.ShowAd)
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(tag, "Ad showed fullscreen content.")
                }
            }
        }
    }
}