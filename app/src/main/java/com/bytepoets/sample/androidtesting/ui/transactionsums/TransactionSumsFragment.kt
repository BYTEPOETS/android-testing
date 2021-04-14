package com.bytepoets.sample.androidtesting.ui.transactionsums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.bytepoets.sample.androidtesting.databinding.FragmentTransactionSumsBinding
import com.bytepoets.sample.androidtesting.databinding.FragmentTransactionsBinding
import com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue.MyEntryWithValueListItemRecyclerViewAdapter
import com.bytepoets.sample.androidtesting.util.ResourceState
import com.bytepoets.sample.androidtesting.util.event.EventHandler
import com.bytepoets.sample.androidtesting.util.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Month
import java.util.*

@AndroidEntryPoint
class TransactionSumsFragment : Fragment() {

    private lateinit var transactionSumsViewModel: TransactionSumsViewModel

    private lateinit var binding: FragmentTransactionSumsBinding

    private val recyclerViewAdapter = MyEntryWithValueListItemRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionSumsBinding.inflate(inflater, container, false)
        transactionSumsViewModel =
            ViewModelProviders.of(this).get(TransactionSumsViewModelImpl::class.java)

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionSumsViewModel.apply {
            transactionSumsList.observe(viewLifecycleOwner, {
                recyclerViewAdapter.submitList(it.data)
                binding.emptyView.text = it.errorMessage
                binding.loadingIndicator.visibility = if (it.status == ResourceState.LOADING) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            })
            binding.calendar.setCalendarListener(object : CalendarListener {
                override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                    transactionSumsViewModel.onTimePeriodSelected(toLocalDate(startDate), toLocalDate(endDate))
                }

                override fun onFirstDateSelected(startDate: Calendar) { }

            })
            val start = LocalDate.of(LocalDate.now().year, Month.JANUARY, 1)
            val end = start.plusYears(1).minusDays(1)
            binding.calendar.setSelectedDateRange(toCalendar(start), toCalendar(end))
            transactionSumsViewModel.onTimePeriodSelected(start, end)
        }
    }

    private fun toLocalDate(calendar: Calendar): LocalDate =
        LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH))

    private fun toCalendar(localDate: LocalDate): Calendar =
        Calendar.getInstance().apply { set(localDate.year, localDate.monthValue-1, localDate.dayOfMonth) }
}