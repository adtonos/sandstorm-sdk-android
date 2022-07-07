package com.siroccomobile.adtonos.sandstormexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.siroccomobile.adtonos.sandstormsdk.api.ATSandstormSDK
import com.siroccomobile.adtonos.sandstormsdk.api.SandstormBannerPosition
import com.siroccomobile.adtonos.sandstormsdk.api.SandstormCallback
import com.siroccomobile.adtonos.sandstormsdk.api.SandstormError
import com.siroccomobile.adtonos.thundersdk.api.AdTonosConsent
import com.siroccomobile.adtonos.thundersdk.api.ThunderCallback
import com.siroccomobile.adtonos.thundersdk.api.ThunderError
import com.siroccomobile.adtonos.thundersdk.api.VastAdType

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        //it can be context from application or activity
        ATSandstormSDK.initialize(context = applicationContext)

        //after initialization we can add a callback to be notified about sdk setup
        waitForStartWithCallback()

        //or simulate flow of asking permissions
        //waitIsStarted()

        /*
        we can ask user about consents for following options:
            - AllowStorage
            - AllowPreciseGeolocation
            - AllowUseForPersonalisedContent
            - AllowProcessing
        when user accepted consents -> use AdTonosConsent.AllowAll
        when user declined consents -> use AdTonosConsent.None
        or
        you can ask user later about permissions and override consents by saveConsents method

        After first invoke start() method, consents are saved in application and we can get it by
        invoke loadLatestConsents method.
        We can also save consents manually by invoke saveConsents method.

        Below example when user accept all consents:
        */

        var consents = ATSandstormSDK.loadLatestConsents()
        if (consents == null) {
            consents = AdTonosConsent.AllowAll
        }

        /*
        It's necessary to set banner container view declared in xml if you want to use banner ads.
        It's also necessary to add banner view to all activity layouts if banner should be visible
        across all activities. Additionally to avoid lifecycle problems add setBannerContainer
        and addLifecycleObserver methods in onResume of all activities.
        */
        ATSandstormSDK.setBannerContainer(findViewById(R.id.sandstormBannerView))
        ATSandstormSDK.addLifecycleObserver(lifecycle)

        /*
        After setting up banner container there is possibility to decide where banner should appear.
        By default will appear at top, but you can change position to top/bottom at any time.
        */
        ATSandstormSDK.setBannerPosition(SandstormBannerPosition.bottom)

        //IGNORE IN LITE VERSION OF SDK
        //Invoke this method with your number eight key before calling start
        ATSandstormSDK.setNumberEightKey("U71E94V86CT9ZXY98ABNMFLQ0Y9B")

        //It can be context from application or activity
        ATSandstormSDK.start(applicationContext, consents)
    }

    private fun waitForStartWithCallback() {
        /*
        After initialization we can add a callback to be notified about sdk setup
        If the permissions are granted (or denied), the onStarted method will be called
        almost immediately after the start method. Callbacks are called on UI thread.
        */
        ATSandstormSDK.addCallback(object : ThunderCallback {
            override fun onStarted() {
                Log.d("ADTONOS", "SDK Started")

                //After sdk is started we can use builder to get vast url
                //You can run it on another thread but later call appropriate methods on UI thread
                Thread {
                    //simulation for some actions in game
                    Thread.sleep(1000)

                    processNext()
                }.start()
            }

            override fun onError(error: ThunderError) {
                Log.e("ADTONOS", error.errorMessage ?: error.toString())
            }
        })
    }

    private fun waitIsStarted() {
        Thread {
            do {
                //simulation for some actions in game and waiting for isStarted
                /*
                The isStarted method returns true when system permissions are obtained from the user.
                Therefore, the first startup may increase the time to wait for the confirmation flag.
                If the permissions are granted (or denied), the isStarted method returns true
                almost immediately after the start method
                 */

                Thread.sleep(1000)
            } while (!ATSandstormSDK.isStarted())
            processNext()
        }.start()
    }

    private fun processNext() {

        // we can add a callback to be notified of events from the player,
        // including that an ad has been downloaded and is ready to play
        ATSandstormSDK.addCallback(object : SandstormCallback {
            override fun onVastAdPaused() {
                Log.d("ADTONOS", "Ad Paused")
            }

            override fun onVastAdPlayStarted() {
                Log.d("ADTONOS", "Ad Play")
            }

            override fun onVastAdsAvailabilityExpired() {
                Log.d("ADTONOS", "Ad Expired")
            }

            override fun onVastAdsEnded() {
                Log.d("ADTONOS", "Ad Ended")
            }

            override fun onVastAdsLoaded() {
                Log.d("ADTONOS", "Ad Loaded")
                ATSandstormSDK.playAd()
            }

            override fun onVastAdsStarted() {
                Log.d("ADTONOS", "Ad Started")
            }

            override fun onVastError(error: SandstormError) {
                Log.d("ADTONOS", "Ad Error")
            }
        })

        //we can use builder to get vast url or pass it to request for ads
        val builder = ATSandstormSDK.createBuilder()
            .setAdTonosKey("KT267qyGPudAugiSt")  // PASS HERE YOUR ADTONOS KEY
            .setLanguage("en")                   // optional param
            .setAdType(VastAdType.bannerAd)      // optional param

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
            val result = ATSandstormSDK.requestForAds(this, builder)
            Log.d("ADTONOS", "Request for ads result: $result")
        }
    }

    override fun onPause() {
        super.onPause()

        //If foreground is lost,then the pause method has to be invoked.
        ATSandstormSDK.pauseAd()
    }

    override fun onDestroy() {
        ATSandstormSDK.dispose()

        super.onDestroy()
    }
}
