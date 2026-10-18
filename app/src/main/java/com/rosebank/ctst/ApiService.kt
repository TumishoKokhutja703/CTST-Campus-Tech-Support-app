package com.rosebank.ctst

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/*
 * ApiService.kt
 * Sets up Retrofit and defines all PHP API endpoints.
 * Change BASE_URL if your InfinityFree subdomain is different.
 */

// Your live website API URL — update if subdomain changes
private const val BASE_URL = "http://ctst-rosebank.infinityfreeapp.com/api/"

// Singleton Retrofit client — created once and reused everywhere
object RetrofitClient {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Each function here matches one PHP file in your api/ folder
interface ApiService {
    @POST("login.php")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponse>

    @POST("register.php")
    suspend fun register(@Body body: Map<String, String>): Response<BasicResponse>

    @POST("submit_ticket.php")
    suspend fun submitTicket(@Body body: Map<String, String>): Response<TicketResponse>

    @GET("my_tickets.php")
    suspend fun getMyTickets(@Query("user_id") userId: Int): Response<TicketsResponse>

    @GET("notifications.php")
    suspend fun getNotifications(@Query("user_id") userId: Int): Response<NotificationsResponse>
}
