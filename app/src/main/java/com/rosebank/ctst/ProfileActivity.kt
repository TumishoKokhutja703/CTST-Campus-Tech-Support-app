package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
 * ProfileActivity.kt — Shows the logged-in user's profile
 *
 * SHOWS:
 * - User's initials in an avatar circle
 * - Full name, role, department, email
 * - Logout button that clears the session
 *
 * All data comes from SharedPreferences (saved at login)
 * so no API call is needed on this screen.
 */
class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Read all user details from SharedPreferences
        val prefs = getSharedPreferences("CTST", Context.MODE_PRIVATE)
        val name  = prefs.getString("userName",  "User")     ?: "User"
        val email = prefs.getString("userEmail", "")         ?: ""
        val role  = prefs.getString("userRole",  "Student")  ?: "Student"
        val dept  = prefs.getString("userDept",  "")         ?: ""

        // Generate initials from the full name
        // e.g. "Jane Doe" → "JD"
        val initials = name.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

        // Fill in the profile views
        findViewById<TextView>(R.id.tvInitials).text    = initials
        findViewById<TextView>(R.id.tvFullName).text    = name
        findViewById<TextView>(R.id.tvRole).text        = "$role · IIE Rosebank College"
        findViewById<TextView>(R.id.tvEmail).text       = email
        findViewById<TextView>(R.id.tvDepartment).text  = dept.ifEmpty { "Not set" }

        // Back button
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Logout button — clear all saved data and return to login
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()  // delete all SharedPreferences data
            val intent = Intent(this, MainActivity::class.java)
            // FLAG_ACTIVITY_NEW_TASK and CLEAR_TASK remove all other activities from the stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home          -> { startActivity(Intent(this, DashboardActivity::class.java)); true }
                R.id.nav_log           -> { startActivity(Intent(this, LogTicketActivity::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, NotificationsActivity::class.java)); true }
                R.id.nav_profile       -> true
                else -> false
            }
        }
    }
}
