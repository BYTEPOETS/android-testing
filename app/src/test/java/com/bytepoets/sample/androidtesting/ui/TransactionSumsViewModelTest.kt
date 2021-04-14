package com.bytepoets.sample.androidtesting.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bytepoets.sample.androidtesting.DummyData
import com.bytepoets.sample.androidtesting.bl.TransactionCalculatorImpl
import com.bytepoets.sample.androidtesting.di.ApiConfiguration
import com.bytepoets.sample.androidtesting.di.ApplicationModule
import com.bytepoets.sample.androidtesting.helper.captureValues
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.bytepoets.sample.androidtesting.ui.transactionsums.TransactionSumsViewModelImpl
import com.bytepoets.sample.androidtesting.util.Resource
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransactionSumsViewModelTest {

    companion object {
        private const val timeout = 2_000L
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val moshi: Moshi = ApplicationModule.provideJsonSerializer()

    private lateinit var mockWebServer: MockWebServer

    private lateinit var sut: TransactionSumsViewModelImpl

    @Before
    fun setUp() {
        val apiClient = ApplicationModule.provideApiClient(
            ApplicationModule.provideOkHttpClient(),
            ApplicationModule.provideJsonSerializer(),
            ApiConfiguration("http://localhost:8080")
        )
        mockWebServer = MockWebServer()
        mockWebServer.start(port = 8080)

        sut = TransactionSumsViewModelImpl(
            apiClient = apiClient,
            calculator = TransactionCalculatorImpl()
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `observe list with unknown server should load list and show error`() {
        mockWebServer.shutdown()

        sut.transactionSumsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.error("An error occurred")
                )
            }
        }
    }

    @Test
    fun `observe list with request error should load list and show error`() {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(400)
        })

        sut.transactionSumsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.error("An error occurred")
                )
            }
        }
    }

    @Test
    fun `observe list with no data should load list and show empty view`() {
        givenTransactionsOnServer()

        sut.transactionSumsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.error("No entries available")
                )
            }
        }
    }

    @Test
    fun `observe list with valid data should load list and show entries`() {
        givenTransactionsOnServer(
            DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_ZERO_AMOUNT
        )

        sut.transactionSumsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.success(
                        data = listOf(
                            EntryWithTintableValue("EUR", "EUR", DummyData.SUM_OF_POSITIVE_AND_NEGATIVE_EUR_AMOUNT, null)
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `observe list with valid data and two currencies should load list and show entries of both currencies`() {
        givenTransactionsOnServer(
            DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT_IN_USD,
            DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_ZERO_AMOUNT
        )

        sut.transactionSumsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.success(
                        data = listOf(
                            EntryWithTintableValue("EUR", "EUR", DummyData.SUM_OF_POSITIVE_AND_NEGATIVE_EUR_AMOUNT, null),
                            EntryWithTintableValue("USD", "USD", DummyData.SUM_OF_POSITIVE_AND_NEGATIVE_USD_AMOUNT, null)
                        )
                    )
                )
            }
        }
    }

    private fun givenTransactionsOnServer(vararg transactions: Transaction) {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(200)
            setBody(
                moshi.adapter(TransactionsResponse::class.java).toJson(
                    TransactionsResponse(
                        data = transactions.toList()
                    )
                )
            )
        })
    }
}