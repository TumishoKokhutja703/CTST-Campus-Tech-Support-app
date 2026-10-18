package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/*
 * DashboardActivity.kt — Home screen after login
 *
 * SHOWS:
 * - Greeting with the user's first name
 * - 3 stat pills: Open / In Progress / Resolved counts
 * - Recent tickets list (last 5)
 * - Bottom navigation bar to other screens
 *
 * FLOW:
 * 1. Read user's name and ID from SharedPreferences
 * 2. Set greeting based on current hour
 * 3. Load tickets from API
 * 4. Count tickets by status and update the stat pills
 * 5. Show tickets in the RecyclerView
 */
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Read the saved user details from SharedPreferences
        val prefs  = getSharedPreferences("CTST", Context.MODE_PRIVATE)
        val name   = prefs.getString("userName", "User") ?: "User"
        val userId = prefs.getInt("userId", 0)

        // Set greeting based on the current hour of the day
        val hour     = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning,"
            hour < 17 -> "Good afternoon,"
            else      -> "Good evening,"
        }
        findViewById<TextView>(R.id.tvGreeting).text = greeting

        // Show only the first name e.g. "Jane" from "Jane Doe"
        val firstName = name.split(" ").firstOrNull() ?: name
        findViewById<TextView>(R.id.tvName).text = firstName

        // Get RecyclerView and empty state views
        val rvTickets = findViewById<RecyclerView>(R.id.rvTickets)
        val tvEmpty   = findViewById<TextView>(R.id.tvEmpty)

        // LinearLayoutManager makes the RecyclerView display items in a vertical list
        rvTickets.layoutManager = LinearLayoutManager(this)

        // Load tickets from the API
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                ApiRepository.getMyTickets(userId)
            }

            result.fold(
                onSuccess = { tickets ->
                    // Count tickets by status to fill the stat pills
                    val openCount     = tickets.count { it.status == "Open" }
                    val progressCount = tickets.count { it.status == "In Progress" }
                    val resolvedCount = tickets.count { it.status == "Resolved" || it.status == "Closed" }

                    findViewById<TextView>(R.id.tvCountOpen).text     = openCount.toString()
                    findViewById<TextView>(R.id.tvCountProgress).text = progressCount.toString()
                    findViewById<TextView>(R.id.tvCountResolved).text = resolvedCount.toString()

                    if (tickets.isEmpty()) {
                        // No tickets yet — show the empty state message
                        tvEmpty.visibility   = View.VISIBLE
                        rvTickets.visibility = View.GONE
                    } else {
                        // Show only the 5 most recent tickets on the dashboard
                        tvEmpty.visibility   = View.GONE
                        rvTickets.visibility = View.VISIBLE
                        rvTickets.adapter = TicketAdapter(tickets.take(5)) { ticket ->
                            // When a ticket is tapped, open TicketDetailActivity
                            val intent = Intent(this@DashboardActivity, TicketDetailActivity::class.java)
                            intent.putExtra("ticket_id",  ticket.ticket_id)
                            intent.putExtra("ticket_ref", ticket.ticket_ref)
                            startActivity(intent)
                        }
                    }
                },
                onFailure = {
                    // Could not load — show empty state
                    tvEmpty.visibility   = View.VISIBLE
                    rvTickets.visibility = View.GONE
                }
            )
        }

        // ── Bottom navigation bar ─────────────────────────────────────────────
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home  // highlight the Tickets tab

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home          -> true  // already here
                R.id.nav_log           -> {
                    startActivity(Intent(this, LogTicketActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_profile       -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // "See all" link — opens full ticket list
        findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            startActivity(Intent(this, MyTicketsActivity::class.java))
        }

        // Logout button — clears SharedPreferences and returns to login
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            prefs.edit().clear().apply()  // delete all saved login data
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
