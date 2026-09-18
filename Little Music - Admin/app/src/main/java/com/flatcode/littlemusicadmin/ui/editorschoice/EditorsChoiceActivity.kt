package com.flatcode.littlemusicadmin.ui.editorschoice

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.model.EditorsChoice
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityEditorsChoiceBinding

class EditorsChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorsChoiceBinding
    var activity: Activity = this@EditorsChoiceActivity
    var list: ArrayList<EditorsChoice>? = null
    var adapter: EditorsChoiceAdapter? = null
    var editorsChoice = EditorsChoice()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.nameSpace.setText(R.string.editors_choice)
        binding.toolbar.back.setOnClickListener { onBackPressed() }

        list = ArrayList()
        adapter = EditorsChoiceAdapter(activity, list!!)
        binding.recyclerView.adapter = adapter

        data
    }

    val data: Unit
        get() {
            list!!.clear()
            for (i in 0..49)
                list!!.add(editorsChoice)
            adapter!!.notifyDataSetChanged()
        }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRestart() {
        data
        super.onRestart()
    }

    override fun onResume() {
        data
        super.onResume()
    }
}
