package com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue

import android.R
import androidx.annotation.ColorRes
import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import java.text.NumberFormat
import java.util.*

data class EntryWithTintableValue(
    val id: String,
    val name: String,
    val value: String?,
    @ColorRes val valueColorRes: Int?
) {
    companion object {

        private fun getCurrencyFormatter(currency: Currency) =
            NumberFormat.getCurrencyInstance(Locale.GERMANY).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
                this.currency = currency
            }

        private fun formatAmount(currencyCode: CurrencyCode, amount: Double): String {
            val currency = Currency.getInstance(currencyCode.name)
            return getCurrencyFormatter(currency).format(amount)
        }


        fun fromTransactions(data: List<Transaction>?): List<EntryWithTintableValue>? {
            return data?.map { transaction ->
                EntryWithTintableValue(
                    id = transaction._id,
                    name = transaction.subject,
                    value = formatAmount(transaction.currency, transaction.amount),
                    valueColorRes = if (transaction.amount < 0) {
                        R.color.holo_red_light
                    } else {
                        null
                    }
                )
            }
        }

        fun fromBalances(data: Map<CurrencyCode, Double>?): List<EntryWithTintableValue>? {
            return data?.map { balance ->
                EntryWithTintableValue(
                    id = balance.key.name,
                    name = balance.key.name,
                    value = formatAmount(balance.key, balance.value),
                    valueColorRes = if (balance.value < 0) {
                        R.color.holo_red_light
                    } else {
                        null
                    }
                )
            }
        }
    }
}