package com.devicesecurity.monitor.ui.history

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.devicesecurity.monitor.databinding.ActivityHistoryBinding
import com.devicesecurity.monitor.ui.main.MainViewModel
import com.devicesecurity.monitor.util.ScoreCalculator
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = HistoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }

        setupChart()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.snapshots.collect { snapshots ->
                    adapter.submitList(snapshots)
                    updateChart(snapshots)
                }
            }
        }
    }

    private fun setupChart() {
        binding.scoreChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                textColor = ContextCompat.getColor(context, android.R.color.white)
                gridColor = ContextCompat.getColor(context, android.R.color.darker_gray)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(context, android.R.color.white)
                setDrawGridLines(false)
            }
        }
    }

    private fun updateChart(snapshots: List<com.devicesecurity.monitor.data.db.entity.SecuritySnapshotEntity>) {
        if (snapshots.isEmpty()) return
        val entries = snapshots.reversed().mapIndexed { index, snapshot ->
            Entry(index.toFloat(), snapshot.overallScore.toFloat())
        }
        val dataSet = LineDataSet(entries, "Security Score").apply {
            color = ContextCompat.getColor(this@HistoryActivity, com.devicesecurity.monitor.R.color.primary)
            setDrawCircles(true)
            circleRadius = 3f
            setCircleColor(ContextCompat.getColor(this@HistoryActivity, com.devicesecurity.monitor.R.color.primary))
            setDrawValues(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(this@HistoryActivity, com.devicesecurity.monitor.R.color.primary)
            fillAlpha = 50
        }
        binding.scoreChart.data = LineData(dataSet)
        binding.scoreChart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
