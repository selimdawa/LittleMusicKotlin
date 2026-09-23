package com.flatcode.littlemusicadmin.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.databinding.ActivitySplashBinding
import com.flatcode.littlemusicadmin.ui.auth.LoginActivity
import com.flatcode.littlemusicadmin.utils.openActivity
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    var context: Context = this@SplashActivity
    var auth: FirebaseAuth? = null
    var timeFinal = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        Handler(Looper.getMainLooper()).postDelayed({ checkUser() }, timeFinal.toLong())
    }

    private fun checkUser() {
        //get current user, if logged in
        val firebaseUser = auth!!.currentUser
        if (firebaseUser == null) {
            context.openActivity<LoginActivity>()
        } else {
            context.openActivity<MainActivity>()
        }
        finish()
    }
}