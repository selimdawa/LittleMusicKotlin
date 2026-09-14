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
import com.flatcode.littlemusic.databinding.ActivityForgetPasswordBinding
import com.flatcode.littlemusic.viewmodel.ForgetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {

    private var binding: ActivityForgetPasswordBinding? = null
    private val viewModel: ForgetPasswordViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("ForgetPasswordActivity Created")

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.noAccount.setOnClickListener {
            VOID.Intent1(this, CLASS.REGISTER)
            finish()
        }
        binding!!.login.setOnClickListener {
            VOID.Intent1(this, CLASS.LOGIN)
            finish()
        }
        binding!!.go.setOnClickListener {
            viewModel.recoverPassword(binding!!.emailEt.text.toString().trim())
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
                            Toast.makeText(this@ForgetPasswordActivity, status.message, Toast.LENGTH_SHORT).show()
                        }
                        is ForgetPasswordViewModel.ForgetPasswordStatus.Error -> {
                            dialog!!.dismiss()
                            Toast.makeText(this@ForgetPasswordActivity, status.message, Toast.LENGTH_SHORT).show()
                        }
                        ForgetPasswordViewModel.ForgetPasswordStatus.Idle -> {}
                    }
                }
            }
        }
    }
}