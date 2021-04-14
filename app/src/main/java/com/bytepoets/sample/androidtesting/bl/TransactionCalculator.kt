package com.bytepoets.sample.androidtesting.bl

import com.bytepoets.sample.androidtesting.network.model.CurrencyCode
import com.bytepoets.sample.androidtesting.network.model.Transaction
import java.time.LocalDate

interface TransactionCalculator {
    fun getBalances(
        transactions: List<Transaction>,
        from: LocalDate = LocalDate.MIN,
        to: LocalDate = LocalDate.now()
    ): Map<CurrencyCode, Double>
}

class TransactionCalculatorImpl : TransactionCalculator {
    override fun getBalances(
        transactions: List<Transaction>,
        from: LocalDate,
        to: LocalDate
    ): Map<CurrencyCode, Double> {
        return transactions
            .groupBy { it.currency }  // Map<CurrencyCode, List<Transaction>>
            .filter { it.key != CurrencyCode.UNKNOWN }  // filter map entry with key UNKNOWN
            .mapValues { (_, transactions) ->  // key-value pair -> (key,new value)
                transactions
//                    .filter { it.createdAt.isEqual(from) || it.createdAt.isEqual(to) ||
//                            it.createdAt.isAfter(from) && it.createdAt.isBefore(to) }
//                    .filter { it.createdAt >= from && it.createdAt <= to }
                    .filter { it.createdAt in from..to }  // kotlin rules
                    .sumByDouble { transaction -> transaction.amount } // reduce list to single double value
            }
    }
}
