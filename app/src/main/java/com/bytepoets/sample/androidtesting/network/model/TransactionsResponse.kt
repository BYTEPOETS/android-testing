package com.bytepoets.sample.androidtesting.network.model

import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.util.*

@JsonClass(generateAdapter = true)
data class TransactionsResponse(
    val data: List<Transaction>
)

@JsonClass(generateAdapter = true)
data class Transaction(
    val _id: String,
    val subject: String,
    val currency: CurrencyCode,
    val amount: Double,
    val createdAt: LocalDate,
)

enum class CurrencyCode {
    UNKNOWN,
    USD,
    EUR,
    CHF,
}

/*
Generate sample data with
https://www.json-generator.com

{
  data: [
    '{{repeat(12, 17)}}',
    {
      _id: '{{objectId()}}',
      name: '{{firstName()}} {{surname()}}',
      gender: '{{gender()}}',
      company: '{{company().toUpperCase()}}',
      email: '{{email()}}',
      phone: '+1 {{phone()}}',
      address: '{{integer(100, 999)}} {{street()}}, {{city()}}, {{state()}}, {{integer(100, 10000)}}',
      subject: '{{lorem(5, "words")}}',
      amount: '{{floating(-4000, 4000, 2)}}',
      currency: '{{random("EUR","USD","CHF")}}',
      createdAt: '{{date(new Date(2014, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ssZ")}}'
    }
  ]
}
 */
