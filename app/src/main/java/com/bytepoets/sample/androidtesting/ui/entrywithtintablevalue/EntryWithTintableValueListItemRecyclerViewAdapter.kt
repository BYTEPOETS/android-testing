package com.bytepoets.sample.androidtesting.ui.entrywithtintablevalue

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bytepoets.sample.androidtesting.R


class MyEntryWithValueListItemRecyclerViewAdapter(private val onItemSelectedListener: OnItemSelectedListener? = null) :
    ListAdapter<EntryWithTintableValue, MyEntryWithValueListItemRecyclerViewAdapter.ViewHolder>(
        AdapterItemDiffCallback()
    ) {

    interface OnItemSelectedListener {
        fun onItemSelected(item: EntryWithTintableValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_entry_with_value, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        onItemSelectedListener?.let { listener ->
            holder.rootView.setOnClickListener {
                listener.onItemSelected(item)
            }
        }
        holder.nameView.text = item.name
        holder.valueView.text = item.value
        if (item.valueColorRes == null) {
            holder.valueView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.black
                )
            )
        } else {
            holder.valueView.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.holo_red_light
                )
            )
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: View = view
        val nameView: TextView = view.findViewById(R.id.name)
        val valueView: TextView = view.findViewById(R.id.value)

        override fun toString(): String {
            return super.toString() + " '" + valueView.text + "'"
        }
    }
}

@SuppressLint("DiffUtilEquals")
class AdapterItemDiffCallback : DiffUtil.ItemCallback<EntryWithTintableValue>() {

    override fun areItemsTheSame(
        oldItem: EntryWithTintableValue,
        newItem: EntryWithTintableValue
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: EntryWithTintableValue,
        newItem: EntryWithTintableValue
    ): Boolean =
        oldItem == newItem
}
