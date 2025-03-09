package com.ondokuzon.apppricing.billing

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private var billingClient: BillingClient? = null
    private val _subscriptionState = MutableStateFlow<SubscriptionState>(SubscriptionState.NotInitialized)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState

    init {
        setupBillingClient()
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    coroutineScope.launch {
                        for (purchase in purchases) {
                            //handlePurchase(purchase)
                        }
                    }
                }
            }
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                } else {

                }
            }

            override fun onBillingServiceDisconnected() {
                _subscriptionState.value = SubscriptionState.NotConnected
            }
        })
    }

    private fun queryPurchases() {
        coroutineScope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            billingClient?.queryPurchasesAsync(params) { billingResult, purchaseList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchaseList.isNotEmpty()) {
                        val activePurchase = purchaseList.firstOrNull { purchase ->
                            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        }
                        
                        if (activePurchase != null) {
                            _subscriptionState.value = SubscriptionState.Active(
                                productId = activePurchase.products.firstOrNull() ?: "",
                                purchaseToken = activePurchase.purchaseToken,
                            )
                            Log.d("BillingManager","Active subscription found: ${activePurchase.products}")
                        } else {
                            _subscriptionState.value = SubscriptionState.NoActiveSubscription
                            Log.d("BillingManager","No active subscription found")
                        }
                    } else {
                        _subscriptionState.value = SubscriptionState.NoActiveSubscription
                        Log.d("BillingManager","No subscriptions found")
                    }
                } else {
                    _subscriptionState.value = SubscriptionState.Error(billingResult.debugMessage)
                    Log.d("BillingManager","Failed to query purchases: ${billingResult.debugMessage}")
                }
            }
        }
    }

    fun disconnect() {
        billingClient?.endConnection()
    }
}

sealed class SubscriptionState {
    data object NotInitialized : SubscriptionState()
    data object NotConnected : SubscriptionState()
    data object NoActiveSubscription : SubscriptionState()
    data class Active(
        val productId: String,
        val purchaseToken: String,
    ) : SubscriptionState()
    data class Error(val message: String) : SubscriptionState()
}
