package com.flatcode.littlemusicadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.CLASSv
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivityPrivacyPolicyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrivacyPolicyActivity : AppCompatActivity() {

    private var binding: ActivityPrivacyPolicyBinding? = null
    var activity: Activity = this@PrivacyPolicyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(
            layoutInflater
        )
        val view = binding!!.root
        setContentView(view)
        binding!!.toolbar.nameSpace.setText(R.string.privacy_policy)
        binding!!.toolbar.back.setOnClickListener { v: View? -> onBackPressed() }
        binding!!.edit.setOnClickListener { v: View? ->
            VOID.Intent1(
                activity,
                CLASSv.PRIVACY_POLICY_EDIT
            )
        }
        VOID.Logo(activity, binding!!.logo)
        privacyPolicy()
    }

    private fun privacyPolicy() {
        val reference = FirebaseDatabase.getInstance().reference.child(DATAv.TOOLS)
            .child(DATAv.PRIVACY_POLICY)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.value.toString()
                binding!!.text.text = name
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onRestart() {
        privacyPolicy()
        super.onRestart()
    }

    override fun onResume() {
        privacyPolicy()
        super.onResume()
    }
}