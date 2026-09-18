package com.flatcode.littlemusic.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.intent1
import com.flatcode.littlemusic.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("AuthActivity Created")

        binding.loginBtn.setOnClickListener { this.intent1(CLASS.LOGIN) }
        binding.skipBtn.setOnClickListener { this.intent1(CLASS.REGISTER) }
    }
}