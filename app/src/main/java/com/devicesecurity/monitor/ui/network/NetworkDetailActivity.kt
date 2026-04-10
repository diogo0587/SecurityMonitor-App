package com.devicesecurity.monitor.ui.network

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.databinding.ActivityNetworkDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NetworkDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNetworkDetailBinding
    private val viewModel: NetworkDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNetworkDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.networkInfo.collect { info ->
                    info?.let {
                        binding.wifiSsid.text = it.wifiSsid
                        binding.wifiSecurity.text = it.wifiSecurityType
                        binding.wifiSecure.text = if (it.isWifiSecure) "Secure" else "NOT SECURE"
                        binding.wifiSecure.setTextColor(if (it.isWifiSecure) getColor(R.color.severity_low) else getColor(R.color.severity_critical))
                        binding.vpnStatus.text = if (it.isVpnActive) "Active" else "Not Active"
                        binding.connectionType.text = it.connectionType
                        binding.bluetoothStatus.text = if (viewModel.bluetoothInfo.value?.isBluetoothEnabled == true) "Enabled" else "Disabled"
                        binding.bluetoothDiscoverable.text = if (viewModel.bluetoothInfo.value?.isDiscoverable == true) "YES - At Risk!" else "No"
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
