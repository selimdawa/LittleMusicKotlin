package com.flatcode.littlemusic.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.flatcode.littlemusic.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.databinding.ActivityLoginBinding
import com.flatcode.littlemusic.ui.main.MainActivity
import com.flatcode.littlemusic.utils.ProgressDialog
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("LoginActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.forget.setOnClickListener { this.openActivity<ForgetPasswordActivity>() }
        binding.noAccount.setOnClickListener { this.openActivity<RegisterActivity>() }
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            viewModel.login(email, password)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginStatus.collect { status ->
                    when (status) {
                        is LoginViewModel.LoginStatus.Loading -> {
                            dialog!!.setMessage(status.message)
                            dialog!!.show()
                        }

                        is LoginViewModel.LoginStatus.Success -> {
                            dialog!!.dismiss()
                            this@LoginActivity.openActivity<MainActivity>(clear = true)
                        }

                        is LoginViewModel.LoginStatus.Error -> {
                            dialog!!.dismiss()
                            Toast.makeText(this@LoginActivity, status.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        LoginViewModel.LoginStatus.Idle -> {}
                    }
                }
            }
        }
    }
}