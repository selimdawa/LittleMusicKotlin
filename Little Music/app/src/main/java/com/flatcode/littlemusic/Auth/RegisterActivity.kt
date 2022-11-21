package com.flatcode.littlemusic.Auth

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASSv
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.Unitimport.THEME
import com.flatcode.littlemusic.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    var context: Context = this@RegisterActivity
    private var auth: FirebaseAuth? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        VOID.Logo(baseContext, binding!!.logo)
        VOID.Intro(baseContext, binding!!.background, binding!!.backWhite, binding!!.backBlack)
        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        binding!!.forget.setOnClickListener { v: View? ->
            VOID.Intent1(
                context,
                CLASSv.FORGET_PASSWORD
            )
        }
        binding!!.login.setOnClickListener {
            VOID.Intent1(context, CLASSv.LOGIN)
            finish()
        }
        binding!!.go.setOnClickListener { validateData() }
    }

    private var name = ""
    private var email = ""
    private var password = ""
    private fun validateData() {
        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        email = binding!!.emailEt.text.toString().trim { it <= ' ' }
        password = binding!!.passwordEt.text.toString().trim { it <= ' ' }
        val cPassword = binding!!.cPasswordEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter you name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(context, "Confirm Password...!", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(context, "Password doesn't match...!", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //show progress
        dialog!!.setMessage("Creating account...")
        dialog!!.show()

        //create user in firebase auth
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { updateUserinfo() }
            .addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(context, DATAv.EMPTY + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserinfo() {
        dialog!!.setMessage("Saving user info...")
        //get current user uid, since user is registered so we can get now
        val id = auth!!.uid

        //setup data to add in db
        val hashMap = HashMap<String, Any>()
        hashMap[DATAv.EMAIL] = DATAv.EMPTY + email
        hashMap[DATAv.ID] = DATAv.EMPTY + id
        hashMap[DATAv.PROFILE_IMAGE] = DATAv.EMPTY + DATAv.BASIC
        hashMap[DATAv.TIMESTAMP] = System.currentTimeMillis()
        hashMap[DATAv.USER_NAME] = DATAv.EMPTY + name
        hashMap[DATAv.VERSION] = DATAv.CURRENT_VERSION

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.USERS)
        assert(id != null)
        ref.child(id!!).setValue(hashMap).addOnSuccessListener {
            //data added to db
            dialog!!.dismiss()
            Toast.makeText(context, "Account created...", Toast.LENGTH_SHORT).show()
            VOID.IntentClear(context, CLASSv.MAIN)
            finish()
        }.addOnFailureListener { e: Exception ->
            //data failed adding to db
            Toast.makeText(context, DATAv.EMPTY + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}