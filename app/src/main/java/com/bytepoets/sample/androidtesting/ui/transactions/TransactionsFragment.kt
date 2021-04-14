package com.bytepoets.sample.androidtesting.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytepoets.sample.androidtesting.databinding.FragmentTransactionsBinding
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.EntryWithTintableValue
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.MyEntryWithValueListItemRecyclerViewAdapter
import com.bytepoets.sample.androidtesting.util.ResourceState
import com.bytepoets.sample.androidtesting.util.event.EventHandler
import com.bytepoets.sample.androidtesting.util.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private lateinit var transactionsViewModel: TransactionsViewModel

    private lateinit var binding: FragmentTransactionsBinding

    private val recyclerViewAdapter =
        MyEntryWithValueListItemRecyclerViewAdapter(onItemSelectedListener = object :
            MyEntryWithValueListItemRecyclerViewAdapter.OnItemSelectedListener {
            override fun onItemSelected(item: EntryWithTintableValue) {
                transactionsViewModel.onItemSelected(item)
            }

        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        transactionsViewModel =
            ViewModelProviders.of(this).get(TransactionsViewModelImpl::class.java)

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionsViewModel.apply {
            transactionsList.observe(viewLifecycleOwner, {
                recyclerViewAdapter.submitList(it.data)
                binding.emptyView.text = it.errorMessage
                binding.loadingIndicator.visibility = if (it.status == ResourceState.LOADING) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            })
            dialogEvent.observe(viewLifecycleOwner, EventObserver(object :
                EventHandler<String> {
                override fun onEventUnHandled(event: String) {
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
                        .setMessage(event)
                        .show()
                }

            }))
        }
    }
}