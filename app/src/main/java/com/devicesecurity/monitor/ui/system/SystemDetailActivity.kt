package com.devicesecurity.monitor.ui.system

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.devicesecurity.monitor.R
import com.devicesecurity.monitor.databinding.ActivitySystemDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SystemDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySystemDetailBinding
    private val viewModel: SystemDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySystemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.systemInfo.collect { info ->
                    info?.let {
                        binding.rootStatus.text = if (it.isRooted) "YES - At Risk!" else "Not detected"
                        binding.rootStatus.setTextColor(if (it.isRooted) getColor(R.color.severity_critical) else getColor(R.color.severity_low))
                        binding.rootDetails.text = if (it.rootDetails.isNotEmpty()) it.rootDetails.joinToString("\n") else "No details"
                        binding.bootloaderStatus.text = if (it.isBootloaderUnlocked) "Unlocked" else "Locked"
                        binding.selinuxStatus.text = it.selinuxStatus
                        binding.unknownSources.text = if (it.isUnknownSourcesEnabled) "Enabled - At Risk!" else "Disabled"
                        binding.adbStatus.text = if (it.isAdbEnabled) "Enabled" else "Disabled"
                        binding.developerStatus.text = if (it.isDeveloperOptionsEnabled) "Enabled" else "Disabled"
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
