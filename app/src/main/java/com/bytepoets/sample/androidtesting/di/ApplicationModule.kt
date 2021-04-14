package com.bytepoets.sample.androidtesting.di

import LocalDateAdapter
import com.bytepoets.sample.androidtesting.BuildConfig
import com.bytepoets.sample.androidtesting.bl.TransactionCalculator
import com.bytepoets.sample.androidtesting.bl.TransactionCalculatorImpl
import com.bytepoets.sample.androidtesting.network.ApiClient
import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.EnumJsonAdapter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.util.*
import javax.inject.Singleton

data class ApiConfiguration(val baseUrl: String)

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideJsonSerializer(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(LocalDate::class.java, LocalDateAdapter())
            .add(
                CurrencyCode::class.java,
                EnumJsonAdapter.create(CurrencyCode::class.java).withUnknownFallback(
                    CurrencyCode.UNKNOWN
                )
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideApiClient(okHttpClient: OkHttpClient, moshi: Moshi, apiConfiguration: ApiConfiguration): ApiClient {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(apiConfiguration.baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
        }
    }.build()

    @Singleton
    @Provides
    fun provideTransactionCalculator(): TransactionCalculator = TransactionCalculatorImpl()
}
