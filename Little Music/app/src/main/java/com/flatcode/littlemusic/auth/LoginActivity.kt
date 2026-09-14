package com.flatcode.littlemusic.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.databinding.ActivityLoginBinding
import com.flatcode.littlemusic.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val viewModel: LoginViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("LoginActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.forget.setOnClickListener { VOID.Intent1(this, CLASS.FORGET_PASSWORD) }
        binding!!.noAccount.setOnClickListener { VOID.Intent1(this, CLASS.REGISTER) }
        binding!!.loginBtn.setOnClickListener {
            val email = binding!!.emailEt.text.toString().trim()
            val password = binding!!.passwordEt.text.toString().trim()
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
                            VOID.IntentClear(this@LoginActivity, CLASS.MAIN)
                        }
                        is LoginViewModel.LoginStatus.Error -> {
                            dialog!!.dismiss()
                            Toast.makeText(this@LoginActivity, status.message, Toast.LENGTH_SHORT).show()
                        }
                        LoginViewModel.LoginStatus.Idle -> {}
                    }
                }
            }
        }
    }
}