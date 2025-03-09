package com.ondokuzon.apppricing.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ondokuzon.apppricing.api.repository.AppPricingRepository
import com.ondokuzon.apppricing.client.PagesRequest
import com.ondokuzon.apppricing.client.toApiModel
import com.ondokuzon.apppricing.data.DeviceIdDataStore
import com.ondokuzon.apppricing.data.SessionCountDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class AppPricingLifecycleObserver(
    private val sessionCountDataStore: SessionCountDataStore,
    private val repository: AppPricingRepository,
    private val deviceIdDataStore: DeviceIdDataStore,
    private val coroutineScope: CoroutineScope
) : Application.ActivityLifecycleCallbacks {

    private var activityCount = 0
    private var isFirstLaunch = true
    private val fragmentCallbacks = AppPricingFragmentLifecycleCallbacks(
        repository = repository,
        deviceIdDataStore = deviceIdDataStore,
        coroutineScope = coroutineScope
    )

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, true)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activityCount == 0) {
            // App came to foreground
            if (isFirstLaunch) {
                coroutineScope.launch {
                    sessionCountDataStore.increaseSessionCount()
                }
                isFirstLaunch = false
            }
        }
        activityCount++

        coroutineScope.launch {
            val pageRequest = PagesRequest(
                deviceId = deviceIdDataStore.getDeviceId(),
                pageName = activity.localClassName
            )
            repository.postPage(pageRequest.toApiModel())
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        activityCount--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
        }
    }
}

internal class AppPricingFragmentLifecycleCallbacks(
    private val repository: AppPricingRepository,
    private val deviceIdDataStore: DeviceIdDataStore,
    private val coroutineScope: CoroutineScope
) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        coroutineScope.launch {
            val pageRequest = PagesRequest(
                deviceId = deviceIdDataStore.getDeviceId(),
                pageName = f.javaClass.simpleName
            )
            repository.postPage(pageRequest.toApiModel())
        }
    }
}
