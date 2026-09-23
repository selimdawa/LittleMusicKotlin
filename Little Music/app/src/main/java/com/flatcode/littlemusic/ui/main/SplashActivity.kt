package com.flatcode.littlemusic.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.databinding.ActivitySplashBinding
import com.flatcode.littlemusic.ui.auth.AuthActivity
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("SplashActivity Created")

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.checkUser()
        }, 2000)

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isUserLoggedIn.collect { isLoggedIn ->
                    isLoggedIn?.let {
                        if (it) {
                            this@SplashActivity.openActivity<MainActivity>()
                        } else {
                            this@SplashActivity.openActivity<AuthActivity>()
                        }
                        finish()
                    }
                }
            }
        }
    }
}