package com.rosebank.ctst

/*
 * Ticket.kt
 * Data class for a ticket stored locally in SQLite on the device.
 * ApiTicket (in ApiModels.kt) is used for tickets from the API.
 * This class is used for local offline storage.
 */
data class Ticket(
    val id: Int = 0,
    val ticketRef: String = "",
    val category: String = "",
    val priority: String = "",
    val status: String = "Open",
    val description: String = "",
    val location: String = "",
    val deviceType: String = "",
    val dateOpened: String = "",
    val attachmentUri: String = ""
)
