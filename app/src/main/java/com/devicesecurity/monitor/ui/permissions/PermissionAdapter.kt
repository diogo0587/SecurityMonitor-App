package com.devicesecurity.monitor.ui.permissions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.databinding.ItemPermissionBinding
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import com.devicesecurity.monitor.domain.model.Severity

class PermissionAdapter : ListAdapter<AppPermissionInfo, PermissionAdapter.PermissionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = ItemPermissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PermissionViewHolder(private val binding: ItemPermissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppPermissionInfo) {
            binding.appName.text = item.appName
            binding.packageName.text = item.packageName
            binding.permissionsText.text = item.permissions.joinToString(", ") { it.substringAfterLast(".") }
            binding.severityBadge.text = if (item.recentlyGranted) {
                "${item.severity.name} • RECENT"
            } else {
                item.severity.name
            }
            val color = when (item.severity) {
                Severity.CRITICAL -> R.color.severity_critical
                Severity.HIGH -> R.color.severity_high
                Severity.MEDIUM -> R.color.severity_medium
                else -> R.color.severity_low
            }
            binding.severityBadge.setBackgroundColor(ContextCompat.getColor(binding.root.context, color))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppPermissionInfo>() {
        override fun areItemsTheSame(oldItem: AppPermissionInfo, newItem: AppPermissionInfo) = oldItem.packageName == newItem.packageName
        override fun areContentsTheSame(oldItem: AppPermissionInfo, newItem: AppPermissionInfo) = oldItem == newItem
    }
}
