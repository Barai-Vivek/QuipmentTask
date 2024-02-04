package com.vivek.quipmenttask.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vivek.quipmenttask.R
import com.vivek.quipmenttask.data.model.Trip
import com.vivek.quipmenttask.databinding.TripsAdapterItemBinding
import com.vivek.quipmenttask.util.Constants
import com.vivek.quipmenttask.util.formatToString


class TripsAdapter(
    private val tripsList: List<Trip>,
    private val selectedItems: HashSet<Int>,
    private val changeItemSelection: (Int) -> Unit,
) :
    RecyclerView.Adapter<TripsAdapter.ViewHolder>() {


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
    }

    inner class ViewHolder(private val binding: TripsAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Trip) {
            binding.apply {
                trip = product
                executePendingBindings()


                tvTrip.apply {
                    val count = "Trip ${(adapterPosition + 1)}"
                    text = count
                }

                tvPickUpTime.apply {
                    text = resources.getString(
                        R.string.display_pickup_time,
                        tripsList[adapterPosition].pickTime.formatToString(Constants.yyyyMMddHHmm)
                    )
                }
                relContainer.background = null

                relContainer.isSelected = selectedItems.contains(adapterPosition)
                relContainer.setOnClickListener {
                    if (selectedItems.contains(adapterPosition)) {
                        relContainer.background = null
                    } else {
                        relContainer.setBackgroundResource(R.drawable.item_bg_selected)
                    }
                    changeItemSelection(adapterPosition)
                }
            }
        }
    }
}