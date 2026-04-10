package com.devicesecurity.monitor.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.databinding.ActivityMainBinding
import com.devicesecurity.monitor.ui.device.StorageBatteryDetailActivity
import com.devicesecurity.monitor.ui.history.HistoryActivity
import com.devicesecurity.monitor.ui.network.NetworkDetailActivity
import com.devicesecurity.monitor.ui.permissions.PermissionDetailActivity
import com.devicesecurity.monitor.ui.settings.SettingsActivity
import com.devicesecurity.monitor.ui.system.SystemDetailActivity
import com.devicesecurity.monitor.util.ScoreCalculator
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var alertsAdapter: AlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupUI()
        observeState()
    }

    private fun setupUI() {
        alertsAdapter = AlertsAdapter()
        binding.alertsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alertsAdapter
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.runScan() }

        binding.cardPermissions.setOnClickListener { startActivity(Intent(this, PermissionDetailActivity::class.java)) }
        binding.cardNetwork.setOnClickListener { startActivity(Intent(this, NetworkDetailActivity::class.java)) }
        binding.cardSystem.setOnClickListener { startActivity(Intent(this, SystemDetailActivity::class.java)) }
        binding.cardDevice.setOnClickListener { startActivity(Intent(this, StorageBatteryDetailActivity::class.java)) }

        setupChart()
    }

    private fun setupChart() {
        binding.scoreChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                textColor = ContextCompat.getColor(context, R.color.text_secondary)
            }
            xAxis.isEnabled = false
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefresh.isRefreshing = state.isScanning
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.contentLayout.visibility = if (state.isLoading) View.GONE else View.VISIBLE

                    state.scanResult?.let { result ->
                        val score = result.securityScore
                        val color = ScoreCalculator.getScoreColor(score.overallScore)
                        val label = ScoreCalculator.getScoreLabel(score.overallScore)

                        binding.scoreText.text = score.overallScore.toString()
                        binding.scoreLabel.text = label
                        binding.scoreText.setTextColor(color)
                        binding.scoreProgress.progress = score.overallScore
                        binding.scoreProgress.setIndicatorColor(color)

                        binding.permissionsScore.text = "${score.permissionScore}/100"
                        binding.networkScore.text = "${score.networkScore}/100"
                        binding.systemScore.text = "${score.systemScore}/100"
                        binding.deviceScore.text = "${score.storageBatteryScore}/100"

                        binding.permissionsStatus.text = "${result.appPermissions.size} apps with risks"
                        binding.networkStatus.text = buildString {
                            append("WiFi: ${if (result.networkInfo.isWifiSecure) "Secure" else "Insecure"}")
                            if (!result.networkInfo.isVpnActive) append(" · No VPN")
                        }
                        binding.systemStatus.text = buildString {
                            append(if (result.systemInfo.isRooted) "Rooted" else "No root")
                            append(" · SELinux: ${result.systemInfo.selinuxStatus}")
                        }
                        binding.deviceStatus.text = buildString {
                            append(if (result.storageBatteryInfo.isEncrypted) "Encrypted" else "Not encrypted")
                            append(" · ${result.storageBatteryInfo.batteryLevel}% battery")
                        }
                    }

                    alertsAdapter.submitList(state.recentAlerts)

                    if (state.snapshots.size > 1) {
                        val entries = state.snapshots.take(10).reversed().mapIndexed { index, snapshot ->
                            Entry(index.toFloat(), snapshot.overallScore.toFloat())
                        }
                        val dataSet = LineDataSet(entries, "Score").apply {
                            color = ContextCompat.getColor(this@MainActivity, R.color.primary)
                            setDrawCircles(false)
                            setDrawValues(false)
                            lineWidth = 2f
                            mode = LineDataSet.Mode.CUBIC_BEZIER
                        }
                        binding.scoreChart.data = LineData(dataSet)
                        binding.scoreChart.invalidate()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.runScan()
    }
}
