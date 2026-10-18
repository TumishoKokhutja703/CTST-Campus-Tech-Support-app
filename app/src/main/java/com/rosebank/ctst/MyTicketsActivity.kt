package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * MyTicketsActivity.kt — Full list of all the user's tickets
 * Similar to the dashboard but shows ALL tickets, not just 5.
 * Accessible from the Dashboard "See all" link or bottom nav.
 */
class MyTicketsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tickets)

        val prefs  = getSharedPreferences("CTST", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", 0)

        val rvTickets = findViewById<RecyclerView>(R.id.rvTickets)
        val tvEmpty   = findViewById<TextView>(R.id.tvEmpty)
        val tvBack    = findViewById<TextView>(R.id.tvBack)

        rvTickets.layoutManager = LinearLayoutManager(this)

        // Back button goes to previous screen
        tvBack.setOnClickListener { finish() }

        // Load all tickets for this user
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                ApiRepository.getMyTickets(userId)
            }
            result.fold(
                onSuccess = { tickets ->
                    if (tickets.isEmpty()) {
                        tvEmpty.visibility   = View.VISIBLE
                        rvTickets.visibility = View.GONE
                    } else {
                        tvEmpty.visibility   = View.GONE
                        rvTickets.visibility = View.VISIBLE
                        // Show ALL tickets — no .take(5) limit here
                        rvTickets.adapter = TicketAdapter(tickets) { ticket ->
                            val intent = Intent(this@MyTicketsActivity, TicketDetailActivity::class.java)
                            intent.putExtra("ticket_id",  ticket.ticket_id)
                            intent.putExtra("ticket_ref", ticket.ticket_ref)
                            startActivity(intent)
                        }
                    }
                },
                onFailure = {
                    tvEmpty.visibility   = View.VISIBLE
                    rvTickets.visibility = View.GONE
                }
            )
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home          -> { finish(); true }
                R.id.nav_log           -> { startActivity(Intent(this, LogTicketActivity::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, NotificationsActivity::class.java)); true }
                R.id.nav_profile       -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }
    }
}
