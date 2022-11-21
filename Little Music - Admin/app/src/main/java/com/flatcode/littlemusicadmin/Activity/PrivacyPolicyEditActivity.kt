package com.flatcode.littlemusicadmin.Activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityPrivacyPolicyEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrivacyPolicyEditActivity : AppCompatActivity() {

    private var binding: ActivityPrivacyPolicyEditBinding? = null
    var context: Context = this@PrivacyPolicyEditActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyEditBinding.inflate(
            layoutInflater
        )
        val view = binding!!.root
        setContentView(view)
        binding!!.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding!!.toolbar.back.setOnClickListener { v: View? -> onBackPressed() }
        binding!!.go.setOnClickListener { v: View? -> validateData() }
        VOID.Logo(context, binding!!.logo)
        privacyPolicy()
    }

    private var description = DATAv.EMPTY
    private fun validateData() {
        description = binding!!.text.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(context, "Enter Privacy Policy...", Toast.LENGTH_SHORT).show()
        } else {
            update()
        }
    }

    private fun update() {
        val hashMap = HashMap<String?, Any>()
        hashMap[DATAv.PRIVACY_POLICY] = DATAv.EMPTY + description
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.TOOLS)
        ref.updateChildren(hashMap).addOnSuccessListener { unused: Void? ->
            Toast.makeText(
                context,
                "Privacy Policy updated...",
                Toast.LENGTH_SHORT
            ).show()
        }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(
                    context,
                    DATAv.EMPTY + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun privacyPolicy() {
        val reference = FirebaseDatabase.getInstance().reference.child(DATAv.TOOLS)
            .child(DATAv.PRIVACY_POLICY)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.value.toString()
                binding!!.text.setText(name)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}