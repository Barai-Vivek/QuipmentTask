package com.vivek.quipmenttask.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.databinding.TripsAdapterItemBinding


class TripsAdapter(private val tripsList: List<Trip>,  private val selectedItems: HashSet<Int>) : RecyclerView.Adapter<TripsAdapter.ViewHolder>() {


    private var _binding: TripsAdapterItemBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            TripsAdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tripsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tripsList[position])

        //default
        holder.itemView.setBackgroundResource(R.drawable.item_bg)

        holder.itemView.isSelected = selectedItems.contains(position)
        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                holder.itemView.setBackgroundResource(R.drawable.item_bg)
            } else {
                selectedItems.add(position)
                holder.itemView.setBackgroundResource(R.drawable.item_bg_selected)
            }
        }
    }

    inner class ViewHolder(private val binding: TripsAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Trip) {
            binding.trip = product
            binding.executePendingBindings()
        }
    }
}