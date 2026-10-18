package com.rosebank.ctst

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/*
 * TicketAdapter.kt
 * Connects a list of ApiTicket objects to the RecyclerView.
 * RecyclerView calls onCreateViewHolder() to create a row view,
 * then onBindViewHolder() to fill it with data for each ticket.
 *
 * The onClick lambda is called when a ticket row is tapped,
 * allowing the activity to open the TicketDetailActivity.
 */
class TicketAdapter(
    private val tickets: List<ApiTicket>,          // the list of tickets to display
    private val onClick: (ApiTicket) -> Unit       // called when a row is tapped
) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    // ViewHolder holds references to the views inside one ticket row (item_ticket.xml)
    // This avoids calling findViewById() repeatedly which would be slow
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRef:    TextView = view.findViewById(R.id.tvRef)
        val tvDesc:   TextView = view.findViewById(R.id.tvDesc)
        val tvMeta:   TextView = view.findViewById(R.id.tvMeta)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    // Called when RecyclerView needs a new row — inflate the item_ticket.xml layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return ViewHolder(view)
    }

    // Returns how many rows to show
    override fun getItemCount() = tickets.size

    // Called for each row — fill the views with data from the ticket at this position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        val ctx    = holder.itemView.context

        // Set the text values
        holder.tvRef.text    = "#${ticket.ticket_ref}"
        holder.tvDesc.text   = ticket.description
        holder.tvMeta.text   = "${ticket.category} · ${ticket.priority} · ${ticket.date_opened.take(10)}"
        holder.tvStatus.text = ticket.status

        // Set status badge colour based on current status
        when (ticket.status) {
            "Open" -> {
                holder.tvStatus.setBackgroundColor(ctx.getColor(R.color.navy_pale))
                holder.tvStatus.setTextColor(ctx.getColor(R.color.navy_light))
            }
            "In Progress" -> {
                holder.tvStatus.setBackgroundColor(ctx.getColor(R.color.amber_light))
                holder.tvStatus.setTextColor(ctx.getColor(R.color.amber))
            }
            "Resolved", "Closed" -> {
                holder.tvStatus.setBackgroundColor(ctx.getColor(R.color.green_light))
                holder.tvStatus.setTextColor(ctx.getColor(R.color.green))
            }
            else -> {
                holder.tvStatus.setBackgroundColor(ctx.getColor(R.color.navy_pale))
                holder.tvStatus.setTextColor(ctx.getColor(R.color.navy))
            }
        }

        // When the row is tapped, call the onClick lambda with this ticket
        holder.itemView.setOnClickListener { onClick(ticket) }
    }
}
