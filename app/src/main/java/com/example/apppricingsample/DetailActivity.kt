package com.example.apppricingsample

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.ondokuzon.apppricing.AppPricingInstance

class DetailActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        findViewById<Button>(R.id.button).setOnClickListener {
            AppPricingInstance.postPage("CustomScreenName")
        }
    }
}