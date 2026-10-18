package com.rosebank.ctst

/*
 * ApiRepository.kt
 * Acts as the middleman between activities and the API.
 * Each function calls the API and returns Result.success or Result.failure.
 */
object ApiRepository {

    suspend fun login(email: String, password: String): Result<ApiUser> {
        return try {
            val body = RetrofitClient.api.login(mapOf("email" to email, "password" to password)).body()
            if (body?.success == true && body.user != null) Result.success(body.user)
            else Result.failure(Exception(body?.message ?: "Login failed."))
        } catch (e: Exception) { Result.failure(Exception("No connection. Check your internet.")) }
    }

    suspend fun register(fullName: String, email: String, password: String, role: String, department: String): Result<String> {
        return try {
            val body = RetrofitClient.api.register(mapOf(
                "full_name" to fullName, "email" to email, "password" to password,
                "role" to role, "department" to department
            )).body()
            if (body?.success == true) Result.success(body.message ?: "Account created.")
            else Result.failure(Exception(body?.message ?: "Registration failed."))
        } catch (e: Exception) { Result.failure(Exception("No connection. Check your internet.")) }
    }

    suspend fun submitTicket(userId: Int, category: String, priority: String, description: String, location: String, deviceType: String): Result<String> {
        return try {
            val body = RetrofitClient.api.submitTicket(mapOf(
                "user_id" to userId.toString(), "category" to category, "priority" to priority,
                "description" to description, "location" to location, "device_type" to deviceType
            )).body()
            if (body?.success == true) Result.success(body.ticket_ref ?: "TKT-???")
            else Result.failure(Exception(body?.message ?: "Could not submit ticket."))
        } catch (e: Exception) { Result.failure(Exception("No connection. Check your internet.")) }
    }

    suspend fun getMyTickets(userId: Int): Result<List<ApiTicket>> {
        return try {
            val body = RetrofitClient.api.getMyTickets(userId).body()
            if (body?.success == true) Result.success(body.tickets ?: emptyList())
            else Result.failure(Exception("Could not load tickets."))
        } catch (e: Exception) { Result.failure(Exception("No connection. Check your internet.")) }
    }

    suspend fun getNotifications(userId: Int): Result<List<ApiNotification>> {
        return try {
            val body = RetrofitClient.api.getNotifications(userId).body()
            if (body?.success == true) Result.success(body.notifications ?: emptyList())
            else Result.failure(Exception("Could not load notifications."))
        } catch (e: Exception) { Result.failure(Exception("No connection. Check your internet.")) }
    }
}
