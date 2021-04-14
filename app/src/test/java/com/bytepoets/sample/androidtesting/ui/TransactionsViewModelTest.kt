package com.bytepoets.sample.androidtesting.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bytepoets.sample.androidtesting.DummyData
import com.bytepoets.sample.androidtesting.di.ApiConfiguration
import com.bytepoets.sample.androidtesting.di.ApplicationModule
import com.bytepoets.sample.androidtesting.helper.captureValues
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.bytepoets.sample.androidtesting.ui.transactions.TransactionsViewModelImpl
import com.bytepoets.sample.androidtesting.util.Resource
import com.bytepoets.sample.androidtesting.util.event.Event
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransactionsViewModelTest {

    companion object {
        private const val timeout = 2_000L
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val moshi: Moshi = ApplicationModule.provideJsonSerializer()

    private lateinit var mockWebServer: MockWebServer

    private lateinit var sut: TransactionsViewModelImpl

    @Before
    fun setUp() {
        val apiClient = ApplicationModule.provideApiClient(
            ApplicationModule.provideOkHttpClient(),
            ApplicationModule.provideJsonSerializer(),
            ApiConfiguration("http://localhost:8080")
        )
        mockWebServer = MockWebServer()
        mockWebServer.start(port = 8080)

        sut = TransactionsViewModelImpl(
            apiClient = apiClient
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `observe list with unknown server should load list and show error`() {
        mockWebServer.shutdown()

        sut.transactionsList.captureValues {
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

        sut.transactionsList.captureValues {
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

        sut.transactionsList.captureValues {
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

        sut.transactionsList.captureValues {
            runBlocking {
                assertSendsValues(
                    timeout,
                    Resource.loading(),
                    Resource.success(
                        data = EntryWithTintableValue.fromTransactions(
                            listOf(
                                DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT,
                                DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT,
                                DummyData.TRANSACTION_WITH_ZERO_AMOUNT
                            )
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `click on an entry should show detail`() {
        givenTransactionsOnServer(
            DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT,
            DummyData.TRANSACTION_WITH_ZERO_AMOUNT
        )

        sut.transactionsList.captureValues transactionList@{
            sut.dialogEvent.captureValues dialogEvent@{
                runBlocking {

                    sut.onItemSelected(
                        EntryWithTintableValue.fromTransactions(listOf(DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT))!!
                            .first()
                    )

                    this@transactionList.assertSendsValues(
                        timeout,
                        Resource.loading(),
                        Resource.success(
                            data = EntryWithTintableValue.fromTransactions(
                                listOf(
                                    DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT,
                                    DummyData.TRANSACTION_WITH_POSITIVE_AMOUNT,
                                    DummyData.TRANSACTION_WITH_ZERO_AMOUNT
                                )
                            )
                        )
                    )

                    this@dialogEvent.assertSendsValues(
                        timeout,
                        Event(DummyData.TRANSACTION_WITH_NEGATIVE_AMOUNT.toString())
                    )
                }
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