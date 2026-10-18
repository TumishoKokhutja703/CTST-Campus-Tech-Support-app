package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
 * SplashActivity.kt — The first screen shown when the app opens
 *
 * WHAT IT DOES:
 * 1. Shows the Rosebank logo, CTST name and a loading spinner for 2 seconds
 * 2. Checks SharedPreferences to see if the user is already logged in
 * 3. If logged in → goes straight to DashboardActivity
 * 4. If not logged in → goes to MainActivity (login screen)
 *
 * WHERE TO PUT THIS FILE:
 * app/src/main/java/com/rosebank/ctst/SplashActivity.kt
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Launch a coroutine — runs the delay without freezing the screen
        lifecycleScope.launch {

            // Wait 2 seconds (2000 milliseconds) while showing the splash screen
            delay(2000)

            // Check SharedPreferences to see if this user is already logged in
            val prefs = getSharedPreferences("CTST", Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

            // Decide which screen to open next
            if (isLoggedIn) {
                // User already logged in — skip login and go straight to dashboard
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            } else {
                // Not logged in — show the login screen
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }

            // finish() removes the splash from the back stack
            // so pressing back from login/dashboard won't return to splash
            finish()
        }
    }
}
