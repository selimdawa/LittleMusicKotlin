package com.flatcode.littlemusic.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
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
                            VOID.Intent1(this@SplashActivity, CLASS.MAIN)
                        } else {
                            VOID.Intent1(this@SplashActivity, CLASS.AUTH)
                        }
                        finish()
                    }
                }
            }
        }
    }
}