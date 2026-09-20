package com.flatcode.littlemusicadmin.ui.editorschoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityEditorsChoiceAddBinding
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.addToEditorsChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class EditorsChoiceAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorsChoiceAddBinding
    private var adapter: EditorsChoiceSongAdapter? = null
    private var editorsChoiceId: Int = 0
    private var oldId: String? = null
    private val viewModel: EditorsChoiceAddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editorsChoiceId = intent.getStringExtra(DATA.EDITORS_CHOICE_ID)?.toInt() ?: 0
        oldId = intent.getStringExtra(DATA.OLD_ID)

        binding.toolbar.nameSpace.setText(R.string.editors_choice)
        binding.toolbar.back.setOnClickListener { finish() }
        binding.toolbar.close.setOnClickListener { finish() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        adapter = EditorsChoiceSongAdapter { song ->
            if (oldId != null) {
                addToEditorsChoice(song.id, editorsChoiceId)
                addToEditorsChoice(oldId, 0)
            } else {
                addToEditorsChoice(song.id, editorsChoiceId)
            }
        }
        binding.recyclerView.adapter = adapter

        binding.all.setOnClickListener { viewModel.setOrderBy(DATA.TIMESTAMP) }
        binding.name.setOnClickListener { viewModel.setOrderBy(DATA.NAME) }
        binding.mostViews.setOnClickListener { viewModel.setOrderBy(DATA.VIEWS_COUNT) }
        binding.mostLoves.setOnClickListener { viewModel.setOrderBy(DATA.LOVES_COUNT) }
        binding.favorites.setOnClickListener { viewModel.setFavoritesOnly(true) }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.songs.collectLatest { songs ->
                binding.toolbar.number.text = MessageFormat.format("( {0} )", songs.size)
                adapter!!.submitList(songs)
                
                if (songs.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                    if (!DATA.searchStatus) {
                        Toast.makeText(this@EditorsChoiceAddActivity, "There is no songs!", Toast.LENGTH_SHORT).show()
                    }
                }
                Timber.d("Songs updated: ${songs.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
            viewModel.setSearchQuery("")
        } else {
            super.onBackPressed()
        }
    }
}
