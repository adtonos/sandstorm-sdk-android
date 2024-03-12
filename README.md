# Module SandstormSDK
## Overview

**SandstormSDK** provides in-game, intermission and bounty audio ads and allows publishers to generate new revenues from world-class brands.
Generator and player for personalized VAST audio advertisement. Sandstorm is all-you-need SDK for your in-app audio advertisement.
The framework provides automatically generated, personalized ads with already prepared player. Use it without additional third party configuration to play the ad in your app.

# Tutorial
Below is a tutorial on how to add the library to your project and how to use it. More information on specific methods can be found in the documentation.

## Minimum requirements

* minSdkVersion is set to 24
* targetSdkVersion must be set at least to 32
* multiDexEnabled shall be set to true

## Installation
### Choose SDK versions
You have to decide which version of SDK you want to use. Two available:

**SandstormSDK** - contains SDK functionality + extra user profiling; this version will ask user for extra permissions (i.e. location)

> Profiling mechanism used is an AI functionality, which predicts the live context of a user (e.g. running, commuting) from sensors present in the device, and then packages them neatly into ID-less behavioural audiences (e.g. joggers, frequent shoppers).

**SandstormLiteSDK** - contains SDK functionality without extra user targeting; this version won't ask user for extra permissions

The method of installation depends on how the library was given. If an aar file was provided then adding to the project is by manual means.
Regardless of how you obtained the library, you must add the following code to the application manifest between the application tags:

```xml
<activity android:name="com.adtonos.thundersdk.ui.ThunderActivity"
    android:theme="@style/AppTheme.Transparent" />
```

### Manual installation

Copy the aar files to the "libs" directory of your project.

#### Add Repository

In the base project gradle file, find the "repositories" section and add the following entry to it, if you want to use full version of library:

```groovy
allprojects {
    repositories {
        ... other repositiories
        maven {
            // Required for full version with additional targeting
            url 'https://repo.numbereight.ai/artifactory/gradle-release-local'
        }
    }
}
```

#### Add dependencies

In the application gradle file, find the "dependencies" section and add the following entries to it.

```groovy
// Required for lite version
implementation('com.adtonos:thunder-lite-sdk:1.0@aar')
implementation('com.adtonos:sandstorm-lite-sdk:1.0@aar')

// Required for full version with additional targeting
implementation('com.adtonos:thunder-sdk:1.0@aar')
implementation('com.adtonos:sandstorm-sdk:1.0@aar')
implementation('ai.numbereight.sdk:nesdk:3.4.0@aar') { transitive = true }
implementation('ai.numbereight.sdk:audiences:3.4.0')

// The following libraries can be updated according to project needs. 
// The versions listed are recommended.
implementation('org.jetbrains.kotlin:kotlin-stdlib:1.7.0')
implementation('androidx.core:core-ktx:1.8.0')
implementation('androidx.appcompat:appcompat:1.4.2')
implementation('org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2')
implementation('androidx.multidex:multidex:2.0.1')
implementation('com.google.guava:guava:31.1-android')
implementation('com.google.android.gms:play-services-ads-identifier:18.0.1')
implementation('com.google.android.exoplayer:exoplayer-core:2.18.1')
implementation('com.google.android.exoplayer:extension-ima:2.18.1')
```

## System permissions

During the first startup, the library will ask the user to grant system permissions. The process is automatic.

The following system permissions are used by the library. They will be merged during build, so you do not need to declare them again in the application manifest.

```xml
<!-- Required for full and lite version -->
<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />

<!-- Required for full version -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />
<uses-feature android:name="android.hardware.location" android:required="false" />
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="false" />
<uses-feature android:name="android.hardware.sensor.barometer" android:required="false" />
<uses-feature android:name="android.hardware.sensor.compass" android:required="false" />
<uses-feature android:name="android.hardware.sensor.light" android:required="false" />
<uses-feature android:name="android.hardware.sensor.proximity" android:required="false" />
```

## Consents

To work properly, the SDK needs consents. Depending on the region, please ask the user if they agree to the following.
In some regions, such as the EU, consent is required to allow third-parties to store data on users devices for example.

### AdTonosConsent.AllowAll

Below is a list of what is included in this option:

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

### AdTonosConsent.None

It is possible to start adTonos with option without accepted consents, but it will not collect data about the user then, so the advertisement will not be personalized appropriately to the user.

## Sequence of steps

* Call ATSandstormSDK.initialize method at initialization time of the app.
* Call ATSandstormSDK.addCallback with Thunder callback to listen for sdk state changes
* Call ATSandstormSDK.start method once the user has agreed to the privacy policy by providing the type of consent.
* Call ATSandstormSDK.createBuilder method and fill AdTonos key with ThunderVastUrlBuilder.setAdTonosKey
* On the instance of ThunderVastUrlBuilder call ThunderVastUrlBuilder.build method to get URL
* Repeat last step every time VAST link is required
* Call ATSandstormSDK.addCallback with Sandstorm callback to listen for vast player changes
* To get ads call ATSandstormSDK.requestAds and pass there instance of ThunderVastUrlBuilder
* Call ATSandstormSDK.playAd / ATSandstormSDK.pauseAd
* If foreground is lost, then the ATSandstormSDK.pauseAd method has to be invoked
* When application is being destroyed call ATThunderSDK.dispose method to free all resources

## Start SDK and create builder

### Initialization

The first necessary step in the project is to call the ATSandstormSDK.Initialize method. It should be called at the start of the application, when the other components are created. The method is safe to be called multiple times. The Initialize method must be called every time the application starts. Below is an example of how this should be done:

```kotlin
ATSandstormSDK.initialize(context = applicationContext)
```

### Profiling key

**This chapter is required for full version with additional targeting.**
Before calling start method please invoke setProfilingKey method with your obtained key to ensure proper work of sdk.

```kotlin
ATSandstormSDK.setProfilingKey("KEY")
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

Pass one of available targeting types as a function parameter:
- AdTonosConsent.None
- AdTonosConsent.AllowAll

```kotlin
ATSandstormSDK.start(context = applicationContext, consent = AdTonosConsent.None)
```

To check if SandstormSDK is started it is possible to use the isStarted variable.

```kotlin
ATSandstormSDK.isStarted()
```

#### Save consents

In case you want to change the consents, you can use the method save.

```kotlin
ATSandstormSDK.saveConsents(context, AdTonosConsent.None)
```

#### Load consents

You can also get the last set consents using the method loadLatestConsent.

```kotlin
ATSandstormSDK.loadLatestConsents()
```

### Generate VAST link

To generate a VAST link, create a builder and then set the AdTonos key. Optionally, if you want to use a language other than preferred in the system, you can set it with ``.setLanguage(lang:String)``. Language must be provided in **ISO-639-1** or **ISO-639-2** format. 

```kotlin
val builder = ATThunderSDK.createBuilder()
    .setAdTonosKey("XXXXX")          // Sets developer key.
    .setLanguage("en")               // Sets user language, if different than a system defined.
    .setAdType(VastAdType.bannerAd)  // Sets the type of ad. By default VastAdType.regular is set.
    .build()
```
* **AdTonos key** - Where to find AdTonosKey? 
It's provided by AdTonos on the portal and can be extracted from the link:

 > [https://play.adtonos.com/xml/XXXXX/vast.xml]()
 
 where XXXXX is the AdTonos key. AdTonos usually provides two links, one for testing purposes and one for release. During development testing key shall be used.

## Sample usage

```kotlin
fun onAppStarted() {
    CoroutineScope(Dispatchers.Default).launch {
        val consents = ATSandstormSDK.loadLatestConsents()

        SandstormAdsPlayback().requestAds(applicationContext)

        if (consents == null) {
            //show dialog on main thread
            withContext(Dispatchers.Main) {
                showPrivacyPolicyDialog()
            }
        } else {
            ATSandstormSDK.start(applicationContext, consents)
        }
    }
}

fun showPrivacyPolicyDialog() {
    // provide result from dialog to onPrivacyPolicyPopupClosed
}

fun onPrivacyPolicyPopupClosed(consents: AdTonosConsent) {
    ATSandstormSDK.start(context = applicationContext, consents = consents)
}
```

To listen for events occurring in sdk you have to create and add ThunderCallback. Callbacks are called on the UI thread, but they should not be blocked.

Usage with Thunder callback:

```kotlin
fun onAppStarted() {
    CoroutineScope(Dispatchers.Default).launch {
        val consents = ATSandstormSDK.loadLatestConsents()

        addCallback(applicationContext)

        if (consents == null) {
            //show dialog on main thread
            withContext(Dispatchers.Main) {
                showPrivacyPolicyDialog()
            }
        } else {
            ATSandstormSDK.start(applicationContext, consents)
        }
    }
}

fun showPrivacyPolicyDialog() {
    // provide result from dialog to onPrivacyPolicyPopupClosed
}

fun onPrivacyPolicyPopupClosed(consents: AdTonosConsent) {
    ATSandstormSDK.start(context = applicationContext, consents = consents)
}

suspend fun addCallback(context: Context) {
    ATSandstormSDK.addCallback(object : ThunderCallback {
        override fun onStarted() {
            Log.d("ADTONOS", "SDK Started")

            CoroutineScope(Dispatchers.Default).launch {
                SandstormAdsPlayback().requestAds(context)
            }
        }

        override fun onError(error: ThunderError) {
            Log.e("ADTONOS", error.errorMessage ?: error.toString())
        }
    })
}

```

## Playing Ads

There are several steps to follow in order to play the ads. First of all, using the ThunderVastUrlBuilder, enter the AdTonos key. The next step is to make a request for ads using the RequestForAds method. The next steps are to wait for the ads to be obtained and to run the ads. A callback can be useful here to provide information about the current state and any errors. Below is an example of a class that will cause an advertisement to play.

There are 2 things to note:

* the requestForAds, clear, dispose, playAd, pauseAd methods must be run on the UI thread. How to call it is left to developers, due to the possibility of using RxJava or coroutines and proper thread management.
* callbacks are called on the UI thread, for this reason they should not be blocked by unnecessary operations.

```kotlin
import com.adtonos.sandstormsdk.api.ATSandstormSDK

class SandstormAdsPlayback: SandstormCallback
{
    private val uiScope = CoroutineScope(Dispatchers.Main)


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

    override fun onVastError(error: AdTonosVastError) {
        Log.e("ADTONOS", "Ad Error")
    }

    suspend fun requestAds(context: Context) {
        /*
        without thunder callback:

        val isStarted = GlobalScope.async {
            do {
                delay(100)
            } while (!ATSandstormSDK.isStarted())
        
        }
        isStarted.await()
        */

        ATSandstormSDK.addCallback(this@SandstormAdsPlayback)

        val builder = ATSandstormSDK.createBuilder()
            .setAdTonosKey("") //PASS HERE YOUR ADTONOS KEY

        withContext(Dispatchers.Main) {
            when (ATSandstormSDK.requestForAds(context, builder)) {
                SandstormAdRequestResult.success -> {
                    Log.d("ADTONOS", "Request started")
                }
                SandstormAdRequestResult.inProgress -> TODO()
                SandstormAdRequestResult.failed -> TODO()
                SandstormAdRequestResult.alreadyPrepared -> TODO()
                SandstormAdRequestResult.invalidKey -> TODO()
                SandstormAdRequestResult.invalidLanguage -> TODO() 
            } 
        }
    } 
}

```

### Call onPause when foreground is lost

If foreground is lost, then the pauseAd method has to be invoked.

```kotlin
// Invoked from main activity
override fun onPause() {
    super.onPause()

    //If foreground is lost,then the pause method has to be invoked.
    ATSandstormSDK.pauseAd()
}
```

### Clearing and Disposing

You can call the clear method at any time, which will stop the playback and clear the current state of the ad playback. After calling the clear method, the ad request must be executed again. The clear method also executes automatically when the ad playback has finished or after critical errors that prevent playback.

```kotlin
ATSandstormSDK.clear()
```

To release the memory used by the library completely, call the dispose method. This is usually done when exiting the application. Note that after the dispose method is executed, the only way to restore ad functionality is to call initialize and start again.

```kotlin
// Invoked from main activity
override fun onDestroy() {
    ATSandstormSDK.dispose()

    super.onDestroy()
}
```

## Banners

The SDK has the ability to play audio ads with a banner. If you want to use such an advertisement use the `setAdType(VastAdType.bannerAd)` method in `ThunderVastURLBuilder`

```kotlin
val builder = ATThunderSDK.createBuilder()
    .setAdTonosKey("XXXXX")          // Sets developer key.
    .setLanguage("en")               // Sets user language, if different than a system defined.
    .setAdType(VastAdType.bannerAd)  // Sets the type of ad. By default VastAdType.regular is set.
    .build()
```

### Banner view setup

It's necessary to set banner container view declared in xml if you want to use banner ads. It's also necessary to add banner view to all activity layouts if banner should be visible across all activities. Additionally to avoid lifecycle problems add setBannerContainer and addLifecycleObserver methods in onResume of all activities.

```xml
<com.adtonos.sandstormsdk.core.SandstormBannerView
    android:id="@+id/sandstormBannerView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

```kotlin
ATSandstormSDK.setBannerContainer(findViewById(R.id.sandstormBannerView))
ATSandstormSDK.addLifecycleObserver(lifecycle)
```

### Setting the banner position

The SDK offers to set the banner position, to do this use the following method:

```kotlin
ATSandstormSDK.setBannerPosition(SandstormBannerPosition.bottom) 
```

It is possible to display the banner view at the **top** of the screen or at the **bottom**.

By default, the banner will be displayed at the **top** of the screen. The method needs to be called before the ads are played.


## Documentation

Please see extended documentation from repository to get information about possible errors and other methods that can be invoked.

# Package com.adtonos.sandstormsdk.api

# Package com.adtonos.thundersdk.api


