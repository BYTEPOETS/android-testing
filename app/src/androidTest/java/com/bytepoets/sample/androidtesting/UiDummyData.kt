package com.bytepoets.sample.androidtesting

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import java.time.LocalDate
import kotlin.random.Random

class UiDummyData private constructor() {

    companion object {
        val TRANSACTION_WITH_POSITIVE_AMOUNT =
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "Dummy Transaction Positive",
                currency = CurrencyCode.EUR,
                amount = 42.98,
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
    }
}