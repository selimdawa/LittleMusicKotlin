package com.flatcode.littlemusicadmin.ui.editorschoice

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.flatcode.littlemusicadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityEditorsChoiceBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.dialogOptionDelete
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorsChoiceActivity : BaseActivity() {

    private lateinit var binding: ActivityEditorsChoiceBinding
    private var adapter: EditorsChoiceAdapter? = null
    private val viewModel: EditorsChoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.editors_choice)
        binding.toolbar.back.setOnClickListener { finish() }

        adapter = EditorsChoiceAdapter(onAddClick = { item ->
            openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(
                    DATA.EDITORS_CHOICE_ID to item.position.toString(), DATA.OLD_ID to null
                )
            )
        }, onChangeClick = { item ->
            openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(
                    DATA.EDITORS_CHOICE_ID to item.position.toString(),
                    DATA.OLD_ID to item.song?.id
                )
            )
        }, onRemoveClick = { item ->
            item.song?.let { song ->
                dialogOptionDelete(
                    song.id,
                    song.name ?: "",
                    DATA.EDITORS_CHOICE,
                    DATA.EDITORS_CHOICE,
                    true,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL
                )
            }
        })
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.editorsChoiceList.collectLatest { list ->
                adapter!!.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }
}
