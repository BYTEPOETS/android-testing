package com.bytepoets.sample.androidtesting

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bytepoets.sample.androidtesting.UiDummyData.Companion.TRANSACTION_WITH_NEGATIVE_AMOUNT
import com.bytepoets.sample.androidtesting.UiDummyData.Companion.TRANSACTION_WITH_POSITIVE_AMOUNT
import com.bytepoets.sample.androidtesting.UiDummyData.Companion.TRANSACTION_WITH_ZERO_AMOUNT
import com.bytepoets.sample.androidtesting.di.ApiConfiguration
import com.bytepoets.sample.androidtesting.di.ConfigurationModule
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import com.bytepoets.sample.androidtesting.ui.transactionsums.TransactionSumsFragment
import com.bytepoets.sample.androidtesting.util.launchFragmentInHiltContainer
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import hasItemsCount
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import withRecyclerView
import javax.inject.Inject


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
@UninstallModules(ConfigurationModule::class)
class TransactionSumsFragmentIntegrationTest {
    @Module
    @InstallIn(SingletonComponent::class)
    object FakeBarModule {
        @Provides
        fun provideTestApiConfiguration() = ApiConfiguration("http://localhost:8080/")
    }

    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    var hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var moshi: Moshi

    @Before
    fun init() {
        hiltRule.inject()
        val idlingResource: IdlingResource = OkHttp3IdlingResource.create("OkHttp", okHttpClient)
        IdlingRegistry.getInstance().register(idlingResource)

        mockWebServer = MockWebServer()
        mockWebServer.start(port = 8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun singleEntryWithAmountInEuroShouldShowSumOfEuro() {
        // GIVEN
        givenTransactionsOnServer(TRANSACTION_WITH_POSITIVE_AMOUNT)

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withId(R.id.list)).check(hasItemsCount(1))
        onView(withText("EUR")).check(matches(isDisplayed()))
        onView(withText("USD")).check(doesNotExist())
        onView(withText(TRANSACTION_WITH_POSITIVE_AMOUNT.subject)).check(doesNotExist())
    }

    @Test
    fun singleEntryWithPositiveAmountShouldBeBlack() {
        // GIVEN
        givenTransactionsOnServer(TRANSACTION_WITH_POSITIVE_AMOUNT)

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withId(R.id.list)).check(hasItemsCount(1))
        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.value))
            .check(matches(hasTextColor(android.R.color.black)))
    }

    @Test
    fun singleEntryWithNegativeAmountShouldBeRed() {
        // GIVEN
        givenTransactionsOnServer(TRANSACTION_WITH_NEGATIVE_AMOUNT)

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withId(R.id.list)).check(hasItemsCount(1))
        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.value))
            .check(matches(hasTextColor(android.R.color.holo_red_light)))
    }

    @Test
    fun singleEntryWithZeroAmountShouldBeBlack() {
        // GIVEN
        givenTransactionsOnServer(TRANSACTION_WITH_ZERO_AMOUNT)

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withId(R.id.list)).check(hasItemsCount(1))
        onView(withRecyclerView(R.id.list).atPositionOnView(0, R.id.value))
            .check(matches(hasTextColor(android.R.color.black)))
    }

    @Test
    fun anElementThatShowsTheSumShouldNotBeClickable() {
        // GIVEN
        givenTransactionsOnServer(
            TRANSACTION_WITH_ZERO_AMOUNT,
            TRANSACTION_WITH_POSITIVE_AMOUNT,
            TRANSACTION_WITH_NEGATIVE_AMOUNT
        )

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withText("EUR")).check(matches(not(isClickable())))
    }

    @Test
    fun noElementsShouldShowEmptyView() {
        // GIVEN
        givenTransactionsOnServer()

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withText("No entries available")).check(matches(isDisplayed()))
    }

    @Test
    fun networkErrorShouldShowError() {
        // GIVEN
        givenResponseWithError()

        // WHEN
        whenLaunchTransactionsFragmentInDummyActivity()

        // THEN
        onView(withText("An error occurred")).check(matches(isDisplayed()))
    }

    private fun whenLaunchTransactionsFragmentInDummyActivity() {
        val fragmentArgs = bundleOf()
        launchFragmentInHiltContainer<TransactionSumsFragment>(fragmentArgs)
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

    private fun givenResponseWithError() {
        mockWebServer.enqueue(MockResponse().apply {
            setResponseCode(400)
        })
    }
}
