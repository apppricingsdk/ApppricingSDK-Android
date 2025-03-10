# AppPricing SDK

AppPricing SDK is an intelligent pricing optimization tool for mobile applications. It analyzes user behavior and characteristics to determine the most appropriate pricing strategy for each individual user. 

By integrating this SDK, your app will receive smart recommendations for which paywall to display based on sophisticated backend analysis that categorizes users into different purchasing tiers (e.g., premium, standard, or basic pricing segments).

## Installation

Add the following to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.ondokuzon:apppricing:1.0.0'
}

settings.gradle {
    repositories {
        maven {
            url = "https://maven.pkg.github.com/apppricingsdk/ApppricingSDK-Android"
            credentials {
                username = GITHUB_USER
                password = GITHUB_TOKEN
            }
        }
    }
}
```

## Getting Started

### 1. Initialize the SDK


```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeAppPricing()
    }

    private fun initializeAppPricing() {
        AppPricingInstance.initialize(
            context = this,
            apiKey = "YOUR_API_KEY", // Required: Your API key from AppPricing Dashboard
            isDebugMode = BuildConfig.DEBUG, // Optional: Enable debug mode for development
            errorCallback = { throwable -> // Optional: Handle SDK errors
                Log.e("AppPricing", "Error: ${throwable.message}")
            },
            loggingCallback = { message -> // Optional: Handle SDK logs
                Log.d("AppPricing", message)
            },
            isLoggingEnabled = BuildConfig.DEBUG // Optional: Enable/disable logging
        )
    }
}
```

### 2. Fetching Plans

After initializing the SDK, you can fetch available plans for the device and show the appropriate paywall:

```kotlin
private fun getPlans() {
    scope.launch {
        try {
            AppPricingInstance.getDevicePlans().collectLatest { response ->
                when (response) {
                    is AppPricingRepositoryPlansResponse.Error -> {
                        Log.d("AppPricingRepositoryPlansResponse", "" + response)
                    }
                    AppPricingRepositoryPlansResponse.Idle -> {
                        Log.d("AppPricingRepositoryPlansResponse", "" + response)
                    }
                    AppPricingRepositoryPlansResponse.Loading -> {
                        Log.d("AppPricingRepositoryPlansResponse", "" + response)
                    }
                    is AppPricingRepositoryPlansResponse.Success -> {
                        val name = response.plans.first().name
                        AppPricingInstance.postPage("FetchPlans: $name")
                        Log.d("AppPricingRepositoryPlansResponse", "" + response.plans.first().name)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AppPricingRepositoryPlansResponse", "" + e.message)
        }
    }
}
```

### Best Practices

1. **Pre-fetch Plans**
   - Call `getDevicePlans()` well before showing the paywall
   - Don't wait for user interaction to fetch plans
   - Cache the response to provide instant paywall display when needed
   - This prevents unnecessary waiting time for users

### Common Issues

1. **Initialization Failures**
   - Verify API key is correct
   - Check internet connectivity
   - Ensure proper Application class setup

## Support

For technical support and inquiries:
- Email: support@apppricing.com
