package com.bytepoets.sample.androidtesting

import com.bytepoets.sample.androidtesting.di.ApplicationModule
import com.bytepoets.sample.androidtesting.di.ConfigurationModule
import com.bytepoets.sample.androidtesting.network.ApiClient
import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ApiClientTest {

    private lateinit var apiClient: ApiClient

    @Before
    fun setUp() {
        apiClient = ApplicationModule.provideApiClient(
            ApplicationModule.provideOkHttpClient(),
            ApplicationModule.provideJsonSerializer(),
            ConfigurationModule.provideApiConfiguration()
        )
    }

    @Test
    fun `when transactions are fetched they are returned successfully`() {
        val transactions = apiClient.getTransactions().execute()

        assertThat(transactions.isSuccessful).isTrue()
        assertThat(transactions.body()).isNotNull()
        val data = transactions.body()!!.data
        assertThat(data).isNotEmpty()
        assertThat(data.map { it._id }).doesNotContain(null)
        assertThat(data.map { it.currency }).doesNotContain(CurrencyCode.UNKNOWN)
    }
}
