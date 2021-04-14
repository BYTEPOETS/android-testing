package com.bytepoets.sample.androidtesting.ui.transactionsums

import androidx.lifecycle.*
import com.bytepoets.sample.androidtesting.bl.TransactionCalculator
import com.bytepoets.sample.androidtesting.network.ApiClient
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.bytepoets.sample.androidtesting.util.Resource
import com.bytepoets.sample.androidtesting.util.ResourceState
import com.bytepoets.sample.androidtesting.util.StateObservableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

interface TransactionSumsViewModel {
    val transactionSumsList: LiveData<Resource<List<EntryWithTintableValue>>>
    fun onTimePeriodSelected(from: LocalDate, to: LocalDate)
}

@HiltViewModel
class TransactionSumsViewModelImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val calculator: TransactionCalculator
) : TransactionSumsViewModel, ViewModel() {

    private var currentTransactions = StateObservableLiveData<Resource<List<Transaction>>>().apply {
        onActiveListener = {
            value = Resource.loading(value?.data)
            apiClient.getTransactions().enqueue(object : Callback<TransactionsResponse> {
                override fun onResponse(
                    call: Call<TransactionsResponse>,
                    response: Response<TransactionsResponse>
                ) {
                    val data = response.body()?.data

                    value = if (response.isSuccessful.not()) {
                        Resource.error("An error occurred")
                    } else if (data.isNullOrEmpty()) {
                        Resource.error("No entries available")
                    } else {
                        Resource.success(data)
                    }
                }

                override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                    value = Resource.error("An error occurred")
                }
            })
        }
    }

    private val selectedTimePeriod = MutableLiveData<Pair<LocalDate, LocalDate>>()

    override val transactionSumsList = Transformations.distinctUntilChanged(
        MediatorLiveData<Resource<List<EntryWithTintableValue>>>().apply {
            fun update() {
                val currentTransactionsValue = currentTransactions.value
                val newData = if (currentTransactionsValue?.data == null) {
                    null
                } else {
                    calculator.getBalances(
                        transactions = currentTransactionsValue.data,
                        from = selectedTimePeriod.value?.first ?: LocalDate.now(),
                        to = selectedTimePeriod.value?.second ?: LocalDate.now()
                    )
                }
                value = Resource(
                    status = currentTransactionsValue?.status ?: ResourceState.SUCCESS,
                    data = EntryWithTintableValue.fromBalances(newData),
                    errorMessage = currentTransactionsValue?.errorMessage
                )
            }

            addSource(currentTransactions) { update() }
            addSource(selectedTimePeriod) { update() }
        })

    override fun onTimePeriodSelected(from: LocalDate, to: LocalDate) {
        selectedTimePeriod.value = from to to
    }
}
