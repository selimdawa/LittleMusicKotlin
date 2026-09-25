package com.flatcode.littlemusic.ui.settings

import android.os.Bundle
import androidx.activity.viewModels
import com.flatcode.littlemusic.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.ActivityPrivacyPolicyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PrivacyPolicyActivity : BaseActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    private val viewModel: PrivacyPolicyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("PrivacyPolicyActivity Created")

        binding.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        observeViewModel()
        viewModel.loadPrivacyPolicy()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.privacyPolicy.collect { privacyPolicy ->
                    binding.text.text = privacyPolicy
                }
            }
        }
    }
}