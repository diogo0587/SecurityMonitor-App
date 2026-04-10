package com.devicesecurity.monitor.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.data.db.entity.AlertEntity
import com.devicesecurity.monitor.databinding.ItemAlertBinding
import com.devicesecurity.monitor.domain.model.Severity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertsAdapter : ListAdapter<AlertEntity, AlertsAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlertViewHolder(private val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(alert: AlertEntity) {
            binding.alertTitle.text = alert.title
            binding.alertMessage.text = alert.message
            binding.alertTime.text = dateFormat.format(Date(alert.timestamp))

            val color = when (alert.severity) {
                "CRITICAL" -> R.color.severity_critical
                "HIGH" -> R.color.severity_high
                "MEDIUM" -> R.color.severity_medium
                else -> R.color.severity_low
            }
            binding.alertIcon.setColorFilter(ContextCompat.getColor(binding.root.context, color))
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<AlertEntity>() {
        override fun areItemsTheSame(oldItem: AlertEntity, newItem: AlertEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AlertEntity, newItem: AlertEntity) = oldItem == newItem
    }
}
