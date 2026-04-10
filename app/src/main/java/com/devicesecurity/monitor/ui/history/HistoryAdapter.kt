package com.devicesecurity.monitor.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity
import com.devicesecurity.monitor.databinding.ItemHistoryBinding
import com.devicesecurity.monitor.util.ScoreCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<SecuritySnapshotEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(item: SecuritySnapshotEntity) {
            binding.timestamp.text = dateFormat.format(Date(item.timestamp))
            binding.overallScore.text = "${item.overallScore}/100"
            binding.overallScore.setTextColor(ScoreCalculator.getScoreColor(item.overallScore))
            binding.permissionScore.text = "P: ${item.permissionScore}"
            binding.networkScore.text = "N: ${item.networkScore}"
            binding.systemScore.text = "S: ${item.systemScore}"
            binding.deviceScore.text = "D: ${item.storageBatteryScore}"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SecuritySnapshotEntity>() {
        override fun areItemsTheSame(oldItem: SecuritySnapshotEntity, newItem: SecuritySnapshotEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SecuritySnapshotEntity, newItem: SecuritySnapshotEntity) = oldItem == newItem
    }
}
