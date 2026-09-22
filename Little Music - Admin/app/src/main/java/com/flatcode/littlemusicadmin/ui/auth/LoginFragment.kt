package com.flatcode.littlemusicadmin.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.openActivity
import com.flatcode.littlemusicadmin.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var mContext: Context
    private var auth: FirebaseAuth? = null
    private var dialog: AlertDialog? = null

    private var email = ""
    private var password = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        dialog = AlertDialog.Builder(mContext).apply {
            setTitle("Please wait...")
            setCancelable(false)
        }.create()

        binding.forget.setOnClickListener { mContext.openActivity<ForgetPasswordActivity>() }
        binding.loginBtn.setOnClickListener { validateData() }
    }

    private fun validateData() {
        email = binding.emailEt.text.toString().trim { it <= ' ' }
        password = binding.passwordEt.text.toString().trim { it <= ' ' }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(mContext, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        dialog!!.setMessage("Logging In...")
        dialog!!.show()
        try {
            auth!!.signInWithEmailAndPassword(email, password).addOnCanceledListener {
                dialog!!.dismiss()
                Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                dialog!!.dismiss()
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(mContext, DATA.EMPTY + e.message, Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener { 
                // handle complete if necessary
            }
        } catch (e: Exception) {
            dialog!!.dismiss()
            Toast.makeText(mContext, DATA.EMPTY + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}