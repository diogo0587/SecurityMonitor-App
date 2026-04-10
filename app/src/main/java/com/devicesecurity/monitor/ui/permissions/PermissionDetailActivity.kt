package com.devicesecurity.monitor.ui.permissions

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.devicesecurity.monitor.databinding.ActivityPermissionDetailBinding
import com.devicesecurity.monitor.domain.model.AppPermissionInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PermissionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionDetailBinding
    private val viewModel: PermissionDetailViewModel by viewModels()
    private lateinit var adapter: PermissionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        adapter = PermissionAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PermissionDetailActivity)
            adapter = this@PermissionDetailActivity.adapter
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.permissions.collect { permissions ->
                    adapter.submitList(permissions)
                    binding.emptyView.visibility = if (permissions.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
