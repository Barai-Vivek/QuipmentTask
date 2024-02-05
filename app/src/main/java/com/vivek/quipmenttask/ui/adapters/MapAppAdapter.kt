package com.vivek.quipmenttask.ui.adapters

import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vivek.quipmenttask.databinding.ItemMapAppBinding

class MapAppAdapter(
    private val mapApps: List<ResolveInfo>,
    private val onItemClicked: (Int) -> Unit
) :
    RecyclerView.Adapter<MapAppAdapter.ViewHolder>() {

    private var _binding: ItemMapAppBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            ItemMapAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mapApps[position])

    }

    override fun getItemCount(): Int {
        return mapApps.size
    }

    inner class ViewHolder(private val binding: ItemMapAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appInfo: ResolveInfo) {
            binding.apply {
                appName.text = appInfo.loadLabel(itemView.context.packageManager)
                itemView.setOnClickListener {
                    onItemClicked(adapterPosition)
                }
                divider.visibility = View.VISIBLE
                if (adapterPosition == mapApps.size - 1) {
                    divider.visibility = View.GONE
                }
            }
        }
    }
}
