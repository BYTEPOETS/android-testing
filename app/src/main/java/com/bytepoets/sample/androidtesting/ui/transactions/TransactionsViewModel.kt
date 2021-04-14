package com.bytepoets.sample.androidtesting.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bytepoets.sample.androidtesting.network.ApiClient
import com.bytepoets.sample.androidtesting.network.model.Transaction
import com.bytepoets.sample.androidtesting.network.model.TransactionsResponse
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.bytepoets.sample.androidtesting.util.Resource
import com.bytepoets.sample.androidtesting.util.StateObservableLiveData
import com.bytepoets.sample.androidtesting.util.event.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

interface TransactionsViewModel {
    val transactionsList: LiveData<Resource<List<EntryWithTintableValue>>>
    val dialogEvent: LiveData<Event<String>>
    fun onItemSelected(item: EntryWithTintableValue)
}

@HiltViewModel
class TransactionsViewModelImpl @Inject constructor(
    private val apiClient: ApiClient
) : TransactionsViewModel, ViewModel() {

    private var currentTransaction: List<Transaction>? = null

    override val transactionsList =
        StateObservableLiveData<Resource<List<EntryWithTintableValue>>>().apply {
            onActiveListener = {
                value = Resource.loading(EntryWithTintableValue.fromTransactions(currentTransaction))
                apiClient.getTransactions().enqueue(object : Callback<TransactionsResponse> {
                    override fun onResponse(
                        call: Call<TransactionsResponse>,
                        response: Response<TransactionsResponse>
                    ) {
                        currentTransaction = response.body()?.data

                        value = if (response.isSuccessful.not()) {
                            Resource.error("An error occurred")
                        } else if (currentTransaction.isNullOrEmpty()) {
                            Resource.error("No entries available")
                        } else {
                            Resource.success(
                                EntryWithTintableValue.fromTransactions(currentTransaction)
                            )
                        }
                    }

                    override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                        value = Resource.error("An error occurred")
                    }
                })
            }
        }

    override val dialogEvent = MutableLiveData<Event<String>>()

    override fun onItemSelected(item: EntryWithTintableValue) {
        currentTransaction?.firstOrNull { it._id == item.id }?.let { transaction ->
            dialogEvent.postValue(Event(transaction.toString()))
        }
    }
}
