package com.siroccomobile.vastplayerexample

import androidx.appcompat.app.AppCompatActivity
import com.siroccomobile.vastplayer.api.AdTonosSDK
import android.os.Bundle
import android.util.Log
import com.siroccomobile.linkgenerator.adtonos.api.AdTonosConsent
import com.siroccomobile.vastplayer.api.AdTonosVastError
import com.siroccomobile.vastplayer.api.AdTonosVastPlayerCallback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //it can be context from application or activity
        AdTonosSDK.initialize(context = applicationContext)

        /*
        we need to ask user about consents for following options:
            - AllowStorage
            - AllowPreciseGeolocation
            - AllowUseForPersonalisedContent
            - AllowProcessing
        when user accepted consents -> use AdTonosConsent.AllowAll
        when user declined consents -> use AdTonosConsent.None

        After first invoke start() method, consents are saved in application and we can get it by
        invoke loadLatestConsents method.
        We can also save consents manually by invoke saveConsents method.

        Below example when user accept all consents:
        */

        var consents = AdTonosSDK.loadLatestConsents()
        if (consents == null) {
            consents = AdTonosConsent.AllowAll
        }

        //it can be context from application or activity
        AdTonosSDK.start(applicationContext, consents)
        waitIsStarted() //simulate flow of asking permissions

    }

    private fun waitIsStarted() {
        Thread {
            do
            {
                //simulation for some actions in game and waiting for isStarted
                /*
                The isStarted method returns true when system permissions are obtained from the user.
                Therefore, the first startup may increase the time to wait for the confirmation flag.
                If the permissions are granted (or denied), the isStarted method returns true
                almost immediately after the start method
                 */

                Thread.sleep(1000)
            } while (!AdTonosSDK.isStarted())
            processNext()
        }.start()
    }

    private fun processNext() {

        // we can add a callback to be notified of events from the player,
        // including that an ad has been downloaded and is ready to play
        AdTonosSDK.addCallback(object: AdTonosVastPlayerCallback {
            override fun onAdTonosVastAdPaused() {
                Log.d("ADTONOS", "Ad Paused")
            }

            override fun onAdTonosVastAdPlayStarted() {
                Log.d("ADTONOS", "Ad Play")
            }

            override fun onAdTonosVastAdsAvailabilityExpired() {
                Log.d("ADTONOS", "Ad Expired")
            }

            override fun onAdTonosVastAdsEnded() {
                Log.d("ADTONOS", "Ad Ended")
            }

            override fun onAdTonosVastAdsLoaded() {
                Log.d("ADTONOS", "Ad Loaded")
                AdTonosSDK.playAd()
            }

            override fun onAdTonosVastAdsStarted() {
                Log.d("ADTONOS", "Ad Started")
            }

            override fun onAdTonosVastError(error: AdTonosVastError) {
                Log.d("ADTONOS", "Ad Error")
            }
        })


        //we can use builder to get vast url or pass it to request for ads
        val builder = AdTonosSDK.createBuilder()
            .setAdTonosKey("") //PASS HERE YOUR ADTONOS KEY
        //  .setLanguage("en") // optional param

        /*
        Before start ad we need create request
        Possible results:
         - alreadyPrepared
         - failed
         - inProgress
         - success - doesn't mean that the ad was loaded
          but that the loading was successfully initiated,
          the information about the download is in the callback
        */

        runOnUiThread {
            val result = AdTonosSDK.requestForAds(this, builder)
            Log.d("ADTONOS", "Request for ads result: $result")
        }

    }


    override fun onPause() {
        super.onPause()
        //If foreground is lost,then the pause method has to be invoked.
        AdTonosSDK.pauseAd()

    }

    override fun onDestroy() {
        AdTonosSDK.dispose()
        super.onDestroy()
    }

}