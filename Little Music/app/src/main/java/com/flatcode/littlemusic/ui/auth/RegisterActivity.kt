package com.flatcode.littlemusic.ui.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.ui.main.MainActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("RegisterActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarRl) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 20
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.go.parent as View) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.forget.setOnClickListener { this.openActivity<ForgetPasswordActivity>() }
        binding.login.setOnClickListener {
            this.openActivity<LoginActivity>()
            finish()
        }
        binding.go.setOnClickListener {
            val name = binding.nameEt.text.toString().trim()
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            val cPassword = binding.cPasswordEt.text.toString().trim()
            viewModel.register(name, email, password, cPassword)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerStatus.collect { status ->
                    when (status) {
                        is RegisterViewModel.RegisterStatus.Loading -> {
                            dialog!!.setMessage(status.message)
                            dialog!!.show()
                        }
                        is RegisterViewModel.RegisterStatus.Success -> {
                            dialog!!.dismiss()
                            Toast.makeText(this@RegisterActivity, "Account created...", Toast.LENGTH_SHORT).show()
                            this@RegisterActivity.openActivity<MainActivity>(clear = true)
                            finish()
                        }
                        is RegisterViewModel.RegisterStatus.Error -> {
                            dialog!!.dismiss()
                            Toast.makeText(this@RegisterActivity, status.message, Toast.LENGTH_SHORT).show()
                        }
                        RegisterViewModel.RegisterStatus.Idle -> {}
                    }
                }
            }
        }
    }
}