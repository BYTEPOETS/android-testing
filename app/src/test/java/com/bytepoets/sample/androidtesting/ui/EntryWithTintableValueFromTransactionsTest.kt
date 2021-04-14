package com.bytepoets.sample.androidtesting.ui

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class EntryWithTintableValueFromTransactionsTest {

    @Test
    fun `null map should return null`() {
        val data = null

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData).isEqualTo(null)
    }

    @Test
    fun `no entry in map should return empty list`() {
        val data = emptyList<Transaction>()

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData!!.size).isEqualTo(0)
    }

    @Test
    fun `positive entry in map should return uncolored entry in list`() {
        val data = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = 196.74,
                createdAt = LocalDate.of(2020, 12, 1)
            )
        )

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(EntryWithTintableValue(data.first()._id, "PLA", "196,74 €", null))
    }

    @Test
    fun `negative entry in map should return red entry in list`() {
        val data = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = -151.21,
                createdAt = LocalDate.of(2020, 12, 1)
            )
        )

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(EntryWithTintableValue(data.first()._id, "PLA", "-151,21 €", android.R.color.holo_red_light))
    }

    @Test
    fun `entry in map without decimals should show 2 decimals in list`() {
        val data = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = 196.0,
                createdAt = LocalDate.of(2020, 12, 1)
            )
        )

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(EntryWithTintableValue(data.first()._id, "PLA", "196,00 €", null))
    }

    @Test
    fun `entry in map with several decimals should show 2 decimals in list`() {
        val data = listOf(
            Transaction(
                _id = Random.nextInt().toString(),
                subject = "PLA",
                currency = CurrencyCode.EUR,
                amount = 196.324,
                createdAt = LocalDate.of(2020, 12, 1)
            )
        )

        val uiData = EntryWithTintableValue.fromTransactions(data)

        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(EntryWithTintableValue(data.first()._id, "PLA", "196,32 €", null))
    }
}