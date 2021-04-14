package com.bytepoets.sample.androidtesting.network

import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiClient {
    @GET("api/json/get/bUbKjRivTm?indent=2")
    fun getTransactions(): Call<TransactionsResponse>
}
