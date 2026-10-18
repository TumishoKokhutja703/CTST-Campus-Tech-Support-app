package com.rosebank.ctst

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 * RegisterActivity.kt — Create a new account
 *
 * FLOW:
 * 1. User fills in name, email, role, department, password, confirm password
 * 2. Validate all fields
 * 3. Call ApiRepository.register() on a background thread
 * 4. On success → show toast → go back to login
 * 5. On failure → show error message
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Get references to all the form fields
        val etName    = findViewById<EditText>(R.id.etName)
        val etEmail   = findViewById<EditText>(R.id.etEmail)
        val spinner   = findViewById<Spinner>(R.id.spinnerRole)
        val etDept    = findViewById<EditText>(R.id.etDepartment)
        val etPass    = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnReg    = findViewById<Button>(R.id.btnRegister)
        val tvError   = findViewById<TextView>(R.id.tvError)
        val tvLogin   = findViewById<TextView>(R.id.tvLogin)

        // Populate the Role spinner with options
        // ArrayAdapter connects the list of strings to the dropdown view
        val roles = listOf("Student", "Lecturer")
        ArrayAdapter(this, android.R.layout.simple_spinner_item, roles).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        // Register button
        btnReg.setOnClickListener {
            val name     = etName.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val role     = spinner.selectedItem.toString()
            val dept     = etDept.text.toString().trim()
            val password = etPass.text.toString().trim()
            val confirm  = etConfirm.text.toString().trim()

            // Validate all fields before hitting the API
            when {
                name.isEmpty()          -> tvError.text = "Enter your full name"
                email.isEmpty()         -> tvError.text = "Enter your email address"
                !email.contains("@")    -> tvError.text = "Enter a valid email address"
                dept.isEmpty()          -> tvError.text = "Enter your department"
                password.length < 6     -> tvError.text = "Password must be at least 6 characters"
                password != confirm     -> tvError.text = "Passwords do not match"
                else -> {
                    btnReg.isEnabled = false
                    btnReg.text      = "Creating..."
                    tvError.text     = ""

                    lifecycleScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            ApiRepository.register(name, email, password, role, dept)
                        }
                        btnReg.isEnabled = true
                        btnReg.text      = "Create Account"

                        result.fold(
                            onSuccess = {
                                // Account created — send user back to login
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Account created! Please sign in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
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

        // "Sign in" link — go back to login screen
        tvLogin.setOnClickListener { finish() }
    }
}
