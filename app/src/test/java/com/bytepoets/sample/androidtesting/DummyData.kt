package com.bytepoets.sample.androidtesting

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import java.time.LocalDate
import kotlin.random.Random

class DummyData private constructor() {

    companion object {
        val TRANSACTION_WITH_POSITIVE_AMOUNT =
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "Dummy Transaction Positive",
                currency = CurrencyCode.EUR,
                amount = 67.34,
                createdAt = LocalDate.now()
            )
        val TRANSACTION_WITH_POSITIVE_AMOUNT_IN_USD =
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "Dummy Transaction Positive",
                currency = CurrencyCode.USD,
                amount = 94651.92,
                createdAt = LocalDate.now()
            )
        val TRANSACTION_WITH_NEGATIVE_AMOUNT =
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "Dummy Transaction Negative",
                currency = CurrencyCode.EUR,
                amount = -42.98,
                createdAt = LocalDate.now()
            )
        val TRANSACTION_WITH_ZERO_AMOUNT =
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "Dummy Transaction Zero",
                currency = CurrencyCode.EUR,
                amount = 0.00,
                createdAt = LocalDate.now()
            )
        const val SUM_OF_POSITIVE_AND_NEGATIVE_EUR_AMOUNT = "24,36 €"
        const val SUM_OF_POSITIVE_AND_NEGATIVE_USD_AMOUNT = "94.651,92 USD"
    }
}