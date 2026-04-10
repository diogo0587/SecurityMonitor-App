package com.devicesecurity.monitor.ui.device

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.databinding.ActivityStorageBatteryDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StorageBatteryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStorageBatteryDetailBinding
    private val viewModel: StorageBatteryDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBatteryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.storageBatteryInfo.collect { info ->
                    info?.let {
                        binding.encryptionStatus.text = if (it.isEncrypted) "Encrypted" else "NOT ENCRYPTED"
                        binding.encryptionStatus.setTextColor(if (it.isEncrypted) getColor(R.color.severity_low) else getColor(R.color.severity_critical))
                        binding.encryptionType.text = it.encryptionStatus
                        binding.totalStorage.text = String.format("%.1f GB", it.totalStorageGb)
                        binding.freeStorage.text = String.format("%.1f GB (%.0f%%)", it.freeStorageGb, (it.freeStorageGb / it.totalStorageGb) * 100)
                        binding.storageProgress.max = it.totalStorageGb.toInt()
                        binding.storageProgress.progress = (it.totalStorageGb - it.freeStorageGb).toInt()
                        binding.batteryLevel.text = "${it.batteryLevel}%"
                        binding.batteryHealth.text = it.batteryHealth
                        binding.batteryTemp.text = "${it.batteryTemperature}°C"
                        binding.chargingStatus.text = if (it.isCharging) "Charging" else "Not Charging"
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
