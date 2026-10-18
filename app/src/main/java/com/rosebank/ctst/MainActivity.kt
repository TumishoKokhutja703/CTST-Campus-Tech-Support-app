package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * MainActivity.kt — Login screen
 * First screen shown when the app opens.
 *
 * FLOW:
 * 1. Check SharedPreferences — if already logged in skip to Dashboard
 * 2. User fills in email + password and taps Sign In
 * 3. Validate fields
 * 4. Call ApiRepository.login() on a background thread (Dispatchers.IO)
 * 5. On success → save user to SharedPreferences → go to DashboardActivity
 * 6. On failure → show error message
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences — small key-value store on the device
        // Stores whether the user is already logged in
        val prefs = getSharedPreferences("CTST", Context.MODE_PRIVATE)

        // If already logged in skip the login screen entirely
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Get references to the views defined in activity_main.xml
        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val tvError    = findViewById<TextView>(R.id.tvError)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Sign In button
        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Check fields before calling the API
            when {
                email.isEmpty()    -> tvError.text = "Please enter your email"
                password.isEmpty() -> tvError.text = "Please enter your password"
                else -> {
                    btnLogin.isEnabled = false
                    btnLogin.text      = "Signing in..."
                    tvError.text       = ""

                    // Launch a coroutine — runs asynchronously without freezing the UI
                    lifecycleScope.launch {
                        // withContext(Dispatchers.IO) runs the network call on a background thread
                        val result = withContext(Dispatchers.IO) {
                            ApiRepository.login(email, password)
                        }

                        // Back on the main thread — safe to update UI
                        btnLogin.isEnabled = true
                        btnLogin.text      = "Sign In"

                        result.fold(
                            onSuccess = { user ->
                                // Save the logged-in user's details to SharedPreferences
                                prefs.edit().apply {
                                    putBoolean("isLoggedIn", true)
                                    putInt("userId",         user.user_id)
                                    putString("userName",    user.full_name)
                                    putString("userEmail",   user.email)
                                    putString("userRole",    user.role)
                                    putString("userDept",    user.department ?: "")
                                    apply()
                                }
                                // Go to the dashboard
                                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                                finish()
                            },
                            onFailure = { error ->
                                tvError.text = error.message
                            }
                        )
                    }
                }
            }
        }

        // Register link — opens RegisterActivity
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
