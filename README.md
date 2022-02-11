# Sandstorm SDK by AdTonos

Sandstorm SDK provides in-game, intermission and bounty audio ads and allows publishers to generate new revenues from world-class brands.
Generator and player for personalized VAST audio advertisement. AdTonosVastPlayer is all-you-need SDK for your in-app audio advertisement.
The framework provides automatically generated, personalized ads with already prepared player. Use it without additional third party configuration to play the ad in your app.
Developers should only use the AdTonosSDK from package: com.siroccomobile.vastplayer.api.AdTonosSDK
Below is a tutorial on how to add the library to your project and how to use it. More information on specific methods can be found in the documentation.

## Minimum requirements

* minSdkVersion is set to 24
* targetSdkVersion must be set at least to 31
* multiDexEnabled shall be set to true

## Installation

The method of installation depends on how the library was given. If an aar file was provided then adding to the project is by manual means.
Regardless of how you obtained the library, you must add the following code to the application manifest between the application tags:

```xml
<activity android:name="com.siroccomobile.linkgenerator.adtonos.ui.AdTonosActivity"
android:theme="@style/AppTheme.Transparent" />
```

### Manual installation

Copy the aar files to the "libs" directory of your project.

#### Add Repository

In the base project gradle file, find the "repositories" section and add the following entry to it:

```groovy
allprojects {
    repositories {
        ... other repositiories
        maven {
            url 'https://repo.numbereight.ai/artifactory/gradle-release-local'
        }
    }
}
```

#### Add dependencies

In the application gradle file, find the "dependencies" section and add the following entries to it.

```groovy
implementation 'com.siroccomobile.adtonos:adtonos-linkgenerator:1.0@aar'
implementation 'com.siroccomobile.adtonos:adtonos-vastplayer:1.0@aar'
implementation('ai.numbereight.sdk:nesdk:3.0.2@aar') {
    transitive = true
}
implementation 'ai.numbereight.sdk:audiences:3.0.2'
implementation 'com.google.android.exoplayer:exoplayer-core:2.16.1'
implementation 'com.google.android.exoplayer:extension-ima:2.16.1'
// The following libraries can be updated according to project needs. 
// The versions listed are recommended.
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
implementation 'com.google.android.gms:play-services-ads-identifier:17.1.0'
implementation 'com.google.guava:guava:28.0-android'
implementation "org.jetbrains.kotlin:kotlin-stdlib:1.5.31"
implementation 'androidx.core:core-ktx:1.7.0'
implementation 'androidx.appcompat:appcompat:1.4.0'
```

## Using the SDK

### Initialization

The first necessary step in the project is to call the AdTonosSDK.Initialize method. It should be called at the start of the application, when the other components are created. The method is safe to be called multiple times. The Initialize method must be called every time the application starts. Below is an example of how this should be done:

```kotlin
AdTonosSDK.initialize(context = applicationContext)
```

### Start

The library requires certain permissions and user consents for data collection.
Therefore, one of the initial screens should show the user the terms and conditions containing
the necessary information about the use and processing of personal data. This is done when
the user first interacts with the application, so this is a good time to call the Start method.
The method takes the consent flag as a parameter and then asks the user for the system permissions
necessary for the application to work. The start method must be called every time the application starts.
Additionally, the LoadLatestConsents method can be used, which returns the most recently granted consents.
Below is an example of how this can be done:

```kotlin

    fun onAppStarted() {
        val consents = AdTonosSDK.loadLatestConsents()
        if (consents == null) {
            showPrivacyPolicyDialog()
        }
        else {
            AdTonosSDK.start(applicationContext, consents)
        }

    }

    fun showPrivacyPolicyDialog() {
        // show privacy policy dialog and provide result to onPrivacyPolicyPopupClosed
    }

    fun onPrivacyPolicyPopupClosed(consents: AdTonosConsent)
    {
        //make sure package is right: com.siroccomobile.vastplayer.api
        AdTonosSDK.start(context = applicationContext, consents = consents)
    }


```

### Playing Ads

There are several steps to follow in order to play the ads. First of all, using the AdTonosVastUrlBuilder, enter the AdTonos key. The next step is to make a request for ads using the RequestForAds method. The next steps are to wait for the ads to be obtained and to run the ads. A callback can be useful here to provide information about the current state and any errors. Below is an example of a class that will cause an advertisement to play.

There are 3 things to note:

* the imported package
* the requestForAds, clear, dispose, playAd, pauseAd methods must be run on the UI thread. How to call it is left to developers, due to the possibility of using RxJava or coroutines and proper thread management.
* callbacks are called on the UI thread, for this reason they should not be blocked by unnecessary operations.

```kotlin
import com.siroccomobile.vastplayer.api.AdTonosSDK

class AdTonosAdsPlayback: AdTonosVastPlayerCallback
{
    private val uiScope = CoroutineScope(Dispatchers.Main)


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
        Log.e("ADTONOS", "Ad Error")
    }

    suspend fun requestAds(context: Context) {
        uiScope.launch {
            withContext(Dispatchers.Default) {
                val isStarted = GlobalScope.async {
                    do {
                        delay(100)
                    } while (!AdTonosSDK.isStarted())

                }
                isStarted.await()

                AdTonosSDK.addCallback(this@AdTonosAdsPlayback)

                val builder = AdTonosSDK.createBuilder()
                    .setAdTonosKey("") //PASS HERE YOUR ADTONOS KEY

                withContext(Dispatchers.Main) {
                    when (AdTonosSDK.requestForAds(context, builder)) {
                        AdTonosAdRequestResult.success -> {
                            Log.d("ADTONOS", "Request started")
                        }
                        AdTonosAdRequestResult.inProgress -> TODO()
                        AdTonosAdRequestResult.failed -> TODO()
                        AdTonosAdRequestResult.alreadyPrepared -> TODO()
                        AdTonosAdRequestResult.invalidKey -> TODO()
                        AdTonosAdRequestResult.invalidLanguage -> TODO()
                    }
                }
            }
        }

    }



    /**
     * Invoked from main activity in onPause method.
     */
    fun onApplicationPause() {
        AdTonosSDK.pauseAd()
    }

    /**
     * Invoked from main activity onDestroy method.
     */
    fun dispose() {
        AdTonosSDK.dispose()
    }

}

```

### Remarks

Where to find AdTonosKey? It's provided by AdTonos on the portal and can be extracted from
the link:

> <https://play.adtonos.com/xml/XXXXX/vast.xml>

where XXXXX is the AdTonos key. AdTonos usually provides two links, one for testing purposes and one for release. During development testing key shall be used.

### Call onPause when foreground is lost

If foreground is lost,then the pauseAd method has to be invoked.

```kotlin
 override fun onPause() {
        super.onPause()
        //If foreground is lost,then the pause method has to be invoked.
        AdTonosSDK.pauseAd()

    }
```

### Clearing and Disposing

You can call the clear method at any time, which will stop the playback and clear the current state of the ad playback. After calling the clear method, the ad request must be executed again. The clear method also executes automatically when the ad playback has finished or after critical errors that prevent playback.

To release the memory used by the library completely, call the dispose method. This is usually done when exiting the application. Note that after the dispose method is executed, the only way to restore ad functionality is to call initialize and start again.

## System permissions

During the first startup, the library will ask the user to grant system permissions. The process is automatic.

The following system permissions are used by the library. They will be merged during build, so you do not need to declare them again in the application manifest.

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
<uses-feature android:name="android.hardware.location" android:required="false" />
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
<uses-feature android:name="android.hardware.sensor.barometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
<uses-feature android:name="android.hardware.sensor.light" android:required="false" />
<uses-feature android:name="android.hardware.sensor.proximity" android:required="false" />
```

## Consents

Below is a list of what is included in the AdTonosConsent.AllowAll option

* `PROCESSING` - Allow processing of data.
* `SENSOR_ACCESS` - Allow use of the device's sensor data.
* `STORAGE` - Allow storing and accessing information on the device.
* `USE_FOR_AD_PROFILES` - Allow use of technology for personalised ads.
* `USE_FOR_PERSONALISED_CONTENT` - Allow use of technology for personalised content.
* `USE_FOR_REPORTING` - Allow use of technology for market research and audience insights.
* `USE_FOR_IMPROVEMENT` - Allow use of technology for improving  products.
* `LINKING_DEVICES` - Allow linking different devices to the user through deterministic or probabilistic means.
* `USE_OF_DEVICE_INFO` - Allow use of automatically provided device information such as manufacturer, model, IP addresses and MAC addresses.
* `USE_FOR_SECURITY` - Allow use of independent identifiers to ensure the secure operation of systems.
* `USE_FOR_DIAGNOSTICS` - Allow processing of diagnostic information using an independent identifier to ensure the correct operation of systems.
* `PRECISE_GEOLOCATION` - Allow use of precise geolocation data (within 500 metres accuracy).

## Documentation

Please see documentation to get information about possible errors and other methods that can be invoked.

# Package com.siroccomobile.vastplayer.api

# Package com.siroccomobile.linkgenerator.adtonos.api


