package com.flatcode.littlemusic.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.flatcode.littlemusic.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.databinding.ActivityForgetPasswordBinding
import com.flatcode.littlemusic.utils.ProgressDialog
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ForgetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val viewModel: ForgetPasswordViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("ForgetPasswordActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.noAccount.setOnClickListener {
            this.openActivity<RegisterActivity>()
            finish()
        }
        binding.login.setOnClickListener {
            this.openActivity<LoginActivity>()
            finish()
        }
        binding.go.setOnClickListener {
            viewModel.recoverPassword(binding.emailEt.text.toString().trim())
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.forgetPasswordStatus.collect { status ->
                    when (status) {
                        is ForgetPasswordViewModel.ForgetPasswordStatus.Loading -> {
                            dialog!!.setMessage(status.message)
                            dialog!!.show()
                        }

                        is ForgetPasswordViewModel.ForgetPasswordStatus.Success -> {
                            dialog!!.dismiss()
                            Toast.makeText(
                                this@ForgetPasswordActivity, status.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ForgetPasswordViewModel.ForgetPasswordStatus.Error -> {
                            dialog!!.dismiss()
                            Toast.makeText(
                                this@ForgetPasswordActivity, status.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                        ForgetPasswordViewModel.ForgetPasswordStatus.Idle -> {}
                    }
                }
            }
        }
    }
}