package com.example.apppricingsample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.apppricingsample.ui.theme.ApppricingSampleTheme
import com.ondokuzon.apppricing.AppPricingInstance
import com.ondokuzon.apppricing.BuildConfig
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryDeviceDataResponse
import com.ondokuzon.apppricing.api.repository.AppPricingRepositoryPlansResponse
import com.ondokuzon.apppricing.client.DeviceDataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var deviceDataResponse by mutableStateOf(DeviceDataResponse.empty())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApppricingSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        onGetPlansClick = {
                            getPlans(this, scope)
                        },
                        response = deviceDataResponse,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }

        AppPricingInstance.initialize(
            context = this,
            apiKey = getString(R.string.app_pricing_api_key),
            isDebug = BuildConfig.DEBUG,
            errorCallback = { throwable ->
                Log.e("AppPricingInstance", "Error: $throwable")
            },
            loggingCallback = { message ->
                Log.d("AppPricingInstance", "Log: $message")
            },
            isLoggingEnabled = true
        )

        lifecycleScope.launch {
            AppPricingInstance.getDeviceDataResponse()?.collect { response ->
                when (response) {
                    is AppPricingRepositoryDeviceDataResponse.Success -> {
                        deviceDataResponse = response.deviceDataResponse
                    }

                    is AppPricingRepositoryDeviceDataResponse.Failed -> {
                    /*    Toast.makeText(
                            this@MainActivity,
                            "Failed to get device data: ${response.throwable.message}",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }

                    is AppPricingRepositoryDeviceDataResponse.Loading,
                    AppPricingRepositoryDeviceDataResponse.Idle -> {
                        // NO - OP
                    }
                }
            }
        }
    }
}

fun getPlans(context: Context, scope: CoroutineScope) {
    scope.launch {
        AppPricingInstance.getDevicePlans().collectLatest { response ->
            when (response) {
                is AppPricingRepositoryPlansResponse.Error -> {
                    Toast.makeText(
                        context,
                        "Plans: $response",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("AppPricingRepositoryPlansResponse", "" + response)
                }

                AppPricingRepositoryPlansResponse.Idle -> {
                    Log.d("AppPricingRepositoryPlansResponse", "" + response)
                }

                AppPricingRepositoryPlansResponse.Loading -> {
                    Log.d("AppPricingRepositoryPlansResponse", "" + response)
                }

                is AppPricingRepositoryPlansResponse.Success -> {
                    Toast.makeText(
                        context,
                        "Plans: ${response.plans}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("AppPricingRepositoryPlansResponse", "" + response)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    response: DeviceDataResponse,
    modifier: Modifier = Modifier,
    onGetPlansClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Status: ${response.status ?: "No status"}",
            style = MaterialTheme.typography.titleMedium
        )
        response.data?.let { data ->
            Text(
                text = "Device ID: ${data.device_id ?: "No device ID"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Application ID: ${data.application_id ?: "No application ID"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "ID: ${data.id ?: "No ID"}",
                style = MaterialTheme.typography.bodyMedium
            )
        } ?: Text(
            text = "No data available",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.size(16.dp))
        val context = LocalContext.current
        Button(onClick = {
            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Open Detail Activity")
        }
        Spacer(modifier = Modifier.size(16.dp))

        Button(onClick = onGetPlansClick) {
            Text("Get Plans")
        }
    }
}
