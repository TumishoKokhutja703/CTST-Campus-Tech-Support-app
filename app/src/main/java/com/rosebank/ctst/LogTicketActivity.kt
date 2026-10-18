package com.rosebank.ctst

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * LogTicketActivity.kt — Submit a new support ticket
 *
 * SHOWS:
 * - Category dropdown (Hardware, Software, Network, Other)
 * - Device type dropdown (PC, Laptop, Projector, Wi-Fi, Printer, Other)
 * - Priority dropdown (Low, Medium, High, Critical)
 * - Location text field
 * - Description text area with character counter
 * - Submit Ticket and Cancel buttons
 *
 * FLOW:
 * 1. User fills in the form
 * 2. Taps Submit Ticket
 * 3. Validate all fields
 * 4. Call ApiRepository.submitTicket() on a background thread
 * 5. On success → show ticket reference → go back to Dashboard
 * 6. On failure → show error
 */
class LogTicketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_ticket)

        // Read the logged-in user's ID from SharedPreferences
        val prefs  = getSharedPreferences("CTST", Context.MODE_PRIVATE)
        val userId = prefs.getInt("userId", 0)

        // Get references to all the form views
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerDevice   = findViewById<Spinner>(R.id.spinnerDevice)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val etLocation      = findViewById<EditText>(R.id.etLocation)
        val etDescription   = findViewById<EditText>(R.id.etDescription)
        val tvCharCount     = findViewById<TextView>(R.id.tvCharCount)
        val tvError         = findViewById<TextView>(R.id.tvError)
        val btnSubmit       = findViewById<Button>(R.id.btnSubmit)
        val btnCancel       = findViewById<Button>(R.id.btnCancel)
        val tvBack          = findViewById<TextView>(R.id.tvBack)

        // ── Populate the spinners (dropdowns) ─────────────────────────────────
        fun makeAdapter(items: List<String>) =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, items).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        spinnerCategory.adapter = makeAdapter(listOf("Hardware", "Software", "Network", "Other"))
        spinnerDevice.adapter   = makeAdapter(listOf("PC", "Laptop", "Projector", "Wi-Fi Router", "Printer", "Other"))
        spinnerPriority.adapter = makeAdapter(listOf("Low", "Medium", "High", "Critical"))

        // ── Character counter for the description field ───────────────────────
        // Updates the counter as the user types e.g. "42 / 500"
        etDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvCharCount.text = "${s?.length ?: 0} / 500"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ── Submit Ticket button ──────────────────────────────────────────────
        btnSubmit.setOnClickListener {
            val category    = spinnerCategory.selectedItem.toString()
            val deviceType  = spinnerDevice.selectedItem.toString()
            val priority    = spinnerPriority.selectedItem.toString()
            val location    = etLocation.text.toString().trim()
            val description = etDescription.text.toString().trim()

            // Validate fields
            when {
                location.isEmpty()    -> tvError.text = "Please enter the location"
                description.isEmpty() -> tvError.text = "Please describe the issue"
                description.length < 10 -> tvError.text = "Please describe the issue in more detail"
                else -> {
                    btnSubmit.isEnabled = false
                    btnSubmit.text      = "Submitting..."
                    tvError.text        = ""

                    lifecycleScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            ApiRepository.submitTicket(
                                userId      = userId,
                                category    = category,
                                priority    = priority,
                                description = description,
                                location    = location,
                                deviceType  = deviceType
                            )
                        }

                        btnSubmit.isEnabled = true
                        btnSubmit.text      = "Submit Ticket"

                        result.fold(
                            onSuccess = { ticketRef ->
                                // Ticket was saved on the server — show success message
                                Toast.makeText(
                                    this@LogTicketActivity,
                                    "Ticket $ticketRef submitted successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Go back to the dashboard
                                startActivity(Intent(this@LogTicketActivity, DashboardActivity::class.java))
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

        // Cancel and Back buttons both go back to previous screen
        btnCancel.setOnClickListener { finish() }
        tvBack.setOnClickListener    { finish() }
    }
}
