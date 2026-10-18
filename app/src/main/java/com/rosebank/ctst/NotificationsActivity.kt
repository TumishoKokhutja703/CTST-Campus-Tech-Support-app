package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * NotificationsActivity.kt — Shows status update notifications
 *
 * SHOWS:
 * - A list of all notifications for the logged-in user
 * - Unread notifications are shown with a blue dot
 * - Read notifications are shown with a grey dot
 *
 * FLOW:
 * 1. Read userId from SharedPreferences
 * 2. Call ApiRepository.getNotifications()
 * 3. Show results in RecyclerView
 */
class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val prefs  = getSharedPreferences("CTST", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", 0)

        val rvNotifications = findViewById<RecyclerView>(R.id.rvNotifications)
        val tvEmpty         = findViewById<TextView>(R.id.tvEmpty)
        val tvBack          = findViewById<TextView>(R.id.tvBack)

        rvNotifications.layoutManager = LinearLayoutManager(this)
        tvBack.setOnClickListener { finish() }

        // Load notifications from the API
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                ApiRepository.getNotifications(userId)
            }
            result.fold(
                onSuccess = { notifications ->
                    if (notifications.isEmpty()) {
                        tvEmpty.visibility          = View.VISIBLE
                        rvNotifications.visibility  = View.GONE
                    } else {
                        tvEmpty.visibility          = View.GONE
                        rvNotifications.visibility  = View.VISIBLE
                        rvNotifications.adapter = NotificationAdapter(notifications)
                    }
                },
                onFailure = {
                    tvEmpty.visibility = View.VISIBLE
                }
            )
        }

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_notifications
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home          -> { startActivity(Intent(this, DashboardActivity::class.java)); true }
                R.id.nav_log           -> { startActivity(Intent(this, LogTicketActivity::class.java)); true }
                R.id.nav_notifications -> true
                R.id.nav_profile       -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
                else -> false
            }
        }
    }
}

/*
 * NotificationAdapter — RecyclerView adapter for the notifications list
 * Shows each notification with a coloured dot (blue = unread, grey = read)
 */
class NotificationAdapter(
    private val items: List<ApiNotification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val tvTime:    TextView = view.findViewById(R.id.tvTime)
        val dotView:   View     = view.findViewById(R.id.dotView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = items[position]
        holder.tvMessage.text = notif.message
        holder.tvTime.text    = notif.created_at.take(16) // "2025-05-10 09:14"

        // Blue dot = unread (is_read == 0), Grey = read (is_read == 1)
        val dotColor = if (notif.is_read == 0) {
            holder.itemView.context.getColor(R.color.navy_light)
        } else {
            holder.itemView.context.getColor(R.color.border)
        }
        holder.dotView.setBackgroundColor(dotColor)
    }
}
