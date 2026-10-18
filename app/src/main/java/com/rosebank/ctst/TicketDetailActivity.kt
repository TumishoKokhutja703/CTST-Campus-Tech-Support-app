package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/*
 * TicketDetailActivity.kt — Shows full details of one ticket
 *
 * Receives the ticket data via Intent extras passed from:
 * - DashboardActivity
 * - MyTicketsActivity
 *
 * For now shows the ticket reference and ID.
 * In Task 2 this will show the full detail, status, and activity/comments.
 */
class TicketDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_detail)

        // Retrieve the ticket info passed from the previous screen via Intent
        // getIntExtra / getStringExtra read values put by intent.putExtra()
        val ticketId  = intent.getIntExtra("ticket_id", 0)
        val ticketRef = intent.getStringExtra("ticket_ref") ?: "Unknown"

        // Display the ticket reference in the toolbar title
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "#$ticketRef"

        // Back button returns to the previous screen
        val tvBack = findViewById<TextView>(R.id.tvBack)
        tvBack.setOnClickListener { finish() }
    }
}
