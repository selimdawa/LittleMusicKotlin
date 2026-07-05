package com.flatcode.littlemusic.auth

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.THEME
import com.flatcode.littlemusic.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private var binding: ActivityAuthBinding? = null
    var context: Context = this@AuthActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.loginBtn.setOnClickListener { VOID.Intent1(context, CLASS.LOGIN) }
        binding!!.skipBtn.setOnClickListener { VOID.Intent1(context, CLASS.REGISTER) }
    }
}