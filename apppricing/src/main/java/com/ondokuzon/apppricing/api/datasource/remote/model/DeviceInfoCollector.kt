package com.ondokuzon.apppricing.api.datasource.remote.model

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import java.util.Locale
import java.util.TimeZone

internal class DeviceInfoCollector(private val context: Context) {
    fun collectDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            // Device Hardware Info
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
            model = Build.MODEL,
            device = Build.DEVICE,
            product = Build.PRODUCT,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            
            // OS Info
            androidVersion = Build.VERSION.RELEASE,
            osVersion = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            buildTime = Build.TIME,
            fingerprint = Build.FINGERPRINT,
            
            // Screen Info
            screenMetrics = getScreenMetrics(),
            
            // App Info
            appVersion = getAppVersion(),
            appVersionCode = getAppVersionCode(),
            packageName = context.packageName,
            firstInstallTime = getFirstInstallTime(),
            lastUpdateTime = getLastUpdateTime(),
            
            // Locale Info
            language = Locale.getDefault().language,
            country = Locale.getDefault().country,
            timeZone = TimeZone.getDefault().id,
            
            // Memory Info
            totalMemory = getTotalMemory(),
            availableMemory = getAvailableMemory(),

            // CPU Info
            numberOfCores = Runtime.getRuntime().availableProcessors(),
        )
    }

    private fun getScreenMetrics(): ScreenMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return ScreenMetrics(
            widthPixels = metrics.widthPixels,
            heightPixels = metrics.heightPixels,
        )
    }

    private fun getAppVersion(): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }

    private fun getAppVersionCode(): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }

    private fun getFirstInstallTime(): Long {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }

    private fun getLastUpdateTime(): Long {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }

    private fun getTotalMemory(): Long {
        return Runtime.getRuntime().totalMemory()
    }

    private fun getAvailableMemory(): Long {
        return Runtime.getRuntime().freeMemory()
    }

    private fun getCpuInfo(): Map<String, String> {
        return try {
            ProcessBuilder().command("cat", "/proc/cpuinfo")
                .start()
                .inputStream
                .bufferedReader()
                .useLines { lines ->
                    lines.filter { it.contains(":") }
                        .map { line ->
                            line.split(":", limit = 2)
                                .map { it.trim() }
                                .let { it[0] to it[1] }
                        }
                        .toMap()
                }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}

data class DeviceInfo(
    // Device Hardware
    val manufacturer: String,
    val brand: String,
    val model: String,
    val device: String,
    val product: String,
    val board: String,
    val hardware: String,
    
    // OS
    val androidVersion: String,
    val osVersion: Int,
    val buildId: String,
    val buildTime: Long,
    val fingerprint: String,
    val os: String = "ANDROID",
    
    // Screen
    val screenMetrics: ScreenMetrics,
    
    // App
    val appVersion: String,
    val appVersionCode: Long,
    val packageName: String,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    
    // Locale
    val language: String,
    val country: String,
    val timeZone: String,
    
    // Memory
    val totalMemory: Long,
    val availableMemory: Long,
    
    // CPU
    val numberOfCores: Int,
)

data class ScreenMetrics(
    val widthPixels: Int,
    val heightPixels: Int,
)