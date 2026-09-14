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
import com.flatcode.littlemusic.databinding.ActivityRegisterBinding
import com.flatcode.littlemusic.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    private val viewModel: RegisterViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("RegisterActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.forget.setOnClickListener { VOID.Intent1(this, CLASS.FORGET_PASSWORD) }
        binding!!.login.setOnClickListener {
            VOID.Intent1(this, CLASS.LOGIN)
            finish()
        }
        binding!!.go.setOnClickListener {
            val name = binding!!.nameEt.text.toString().trim()
            val email = binding!!.emailEt.text.toString().trim()
            val password = binding!!.passwordEt.text.toString().trim()
            val cPassword = binding!!.cPasswordEt.text.toString().trim()
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
                            VOID.IntentClear(this@RegisterActivity, CLASS.MAIN)
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