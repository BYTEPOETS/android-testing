package com.bytepoets.sample.androidtesting.ui

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EntryWithTintableValueFromBalancesTest {

    @Test
    fun `null should return null`() {
        val data = null

        val uiData = EntryWithTintableValue.fromBalances(data)

        assertThat(uiData).isNull()
    }

    @Test
    fun `no entry in map should return empty list`() {
        val data = emptyMap<CurrencyCode, Double>()

        val uiData = EntryWithTintableValue.fromBalances(data)

        assertThat(uiData).isEmpty()
    }

    @Test
    fun `positive entry in map should return uncolored entry in list`() {
        val data = mapOf<CurrencyCode, Double>(
            CurrencyCode.EUR to 42.9
        )

        val uiData = EntryWithTintableValue.fromBalances(data)

        assertThat(uiData).isNotNull()
        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(
            EntryWithTintableValue(
                id = CurrencyCode.EUR.name,
                name = CurrencyCode.EUR.name,
                value = "42,90 €",
                valueColorRes = null
            )
        )
    }

    @Test
    fun `negative entry in map should return colored entry in list`() {
        val data = mapOf<CurrencyCode, Double>(
            CurrencyCode.EUR to -42.9
        )

        val uiData = EntryWithTintableValue.fromBalances(data)

        assertThat(uiData).isNotNull()
        assertThat(uiData!!.size).isEqualTo(1)
        assertThat(uiData.first()).isEqualTo(
            EntryWithTintableValue(
                id = CurrencyCode.EUR.name,
                name = CurrencyCode.EUR.name,
                value = "-42,90 €",
                valueColorRes = android.R.color.holo_red_light
            )
        )
    }
}