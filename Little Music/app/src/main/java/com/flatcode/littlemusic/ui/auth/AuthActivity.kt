package com.flatcode.littlemusic.ui.auth

import android.os.Bundle
import com.flatcode.littlemusic.utils.BaseActivity
import com.flatcode.littlemusic.databinding.ActivityAuthBinding
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("AuthActivity Created")

        binding.loginBtn.setOnClickListener { this.openActivity<LoginActivity>() }
        binding.skipBtn.setOnClickListener { this.openActivity<RegisterActivity>() }
    }
}