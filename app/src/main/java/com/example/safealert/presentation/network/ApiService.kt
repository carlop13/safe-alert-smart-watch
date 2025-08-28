package com.example.safealert.presentation.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("send")
    suspend fun sendAlert(@Body email: EmailRequest): Response<Void>
}

data class EmailRequest(val email: String)