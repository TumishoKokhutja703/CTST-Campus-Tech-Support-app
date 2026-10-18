package com.rosebank.ctst

/*
 * ApiModels.kt
 * Data classes matching the JSON responses from your PHP API.
 * Gson automatically maps JSON keys to these field names.
 */

// Response from login.php — {"success": true, "user": {...}}
data class LoginResponse(val success: Boolean, val message: String?, val user: ApiUser?)

// The user object inside login response
data class ApiUser(val user_id: Int, val full_name: String, val email: String, val role: String, val department: String?)

// Generic response — {"success": true, "message": "Done."}
data class BasicResponse(val success: Boolean, val message: String?)

// Response from submit_ticket.php — {"success": true, "ticket_ref": "TKT-001"}
data class TicketResponse(val success: Boolean, val message: String?, val ticket_ref: String?)

// Response from my_tickets.php — {"success": true, "tickets": [...]}
data class TicketsResponse(val success: Boolean, val tickets: List<ApiTicket>?)

// One ticket from the API
data class ApiTicket(
    val ticket_id: Int,
    val ticket_ref: String,
    val category: String,
    val priority: String,
    val status: String,
    val description: String,
    val location: String?,
    val device_type: String?,
    val date_opened: String
)

// Response from notifications.php
data class NotificationsResponse(val success: Boolean, val notifications: List<ApiNotification>?)

// One notification
data class ApiNotification(
    val notif_id: Int,
    val message: String,
    val is_read: Int,     // 0 = unread (blue dot), 1 = read (grey dot)
    val created_at: String,
    val ticket_id: Int?
)
