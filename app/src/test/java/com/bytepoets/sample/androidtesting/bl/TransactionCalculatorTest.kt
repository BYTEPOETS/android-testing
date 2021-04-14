package com.bytepoets.sample.androidtesting.bl

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class TransactionCalculatorTest {

    private val sut = TransactionCalculatorImpl()

    private lateinit var transactions: List<Transaction>

    @Test
    fun `should calculate euro balance correctly`() {
        givenTransactionsWithTotalOf150EuroDebtAnd200DollarSurplus()
        val balances = sut.getBalances(transactions)
        assertThat(balances[CurrencyCode.EUR]).isEqualTo(-150.0)
    }

    @Test
    fun `balance list should contain every currency code in transaction list`() {
        givenTransactionsWithTotalOf150EuroDebtAnd200DollarSurplus()
        val balances = sut.getBalances(transactions)
        assertThat(balances).containsKey(CurrencyCode.EUR)
        assertThat(balances).containsKey(CurrencyCode.USD)
    }

    @Test
    fun `balance list should not contain unknown currency code`() {
        givenTransactionsWithUnknownCurrencies()
        val balances = sut.getBalances(transactions)
        assertThat(balances).doesNotContainKey(CurrencyCode.UNKNOWN)
    }

    @Test
    fun `calculation of balances should only consider transactions within from and to dates`() {
        givenTransactionsWithTotalOf150EuroDebtAnd200DollarSurplus()

        val balances = sut.getBalances(
            transactions,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 12, 31)
        )

        assertThat(balances).isEqualTo(
            mapOf(CurrencyCode.EUR to -250.0, CurrencyCode.USD to 100.0)
        )
    }

    fun givenTransactionsWithTotalOf150EuroDebtAnd200DollarSurplus() {
        transactions = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = -150.0,
                createdAt = LocalDate.of(2020, 12, 1)
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "SSI",
                currency = CurrencyCode.USD,
                amount = 100.0,
                createdAt = LocalDate.of(2020, 1, 1)
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "KHS",
                currency = CurrencyCode.USD,
                amount = 100.0,
                createdAt = LocalDate.of(2021, 1, 1)
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "MBA",
                currency = CurrencyCode.EUR,
                amount = 100.0,
                createdAt = LocalDate.of(2018, 3, 2)
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = -100.0,
                createdAt = LocalDate.of(2020, 7, 6)
            ),
        )
    }

    fun givenTransactionsWithUnknownCurrencies() {
        transactions = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = -150.0,
                createdAt = LocalDate.now()
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "SSI",
                currency = CurrencyCode.UNKNOWN,
                amount = 100.0,
                createdAt = LocalDate.now()
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "KHS",
                currency = CurrencyCode.CHF,
                amount = 100.0,
                createdAt = LocalDate.now()
            ),
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "MBA",
                currency = CurrencyCode.UNKNOWN,
                amount = 100.0,
                createdAt = LocalDate.now()
            ),
        )
    }
}
