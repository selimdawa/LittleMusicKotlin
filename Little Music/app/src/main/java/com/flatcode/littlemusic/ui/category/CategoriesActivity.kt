package com.flatcode.littlemusic.ui.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import android.widget.ImageView
import com.flatcode.littlemusic.utils.checkInterested
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.databinding.ActivityCategoriesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private val viewModel: CategoriesViewModel by viewModels()
    private var adapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("CategoriesActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            windowInsets
        }

        setupToolbar()
        setupRecyclerView()
        setupSwitchBar()
        observeViewModel()

        viewModel.getData()
    }

    private fun setupToolbar() {
        binding.toolbar.nameSpace.setText(R.string.categories)
        binding.toolbar.close.setOnClickListener { onBackPressed() }
        binding.toolbar.back.setOnClickListener { onBackPressed() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }
        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    //None
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onItemClick = { category ->
                openActivity<CategorySongsActivity>(
                    extras = arrayOf(DATA.CATEGORY_ID to category.id, DATA.CATEGORY_NAME to category.name)
                )
            },
            onInterestedClick = { category, view -> (view as? ImageView)?.checkInterested(DATA.CATEGORIES, category.id) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwitchBar() {
        binding.switchBar.all.setOnClickListener { viewModel.setType(DATA.TIMESTAMP) }
        binding.switchBar.mostSongs.setOnClickListener { viewModel.setType(DATA.SONGS_COUNT) }
        binding.switchBar.mostAlbums.setOnClickListener { viewModel.setType(DATA.ALBUMS_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.setType(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.setType(DATA.NAME) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collect { categories ->
                        adapter?.setList(categories)
                        binding.toolbar.number.text = MessageFormat.format("( {0} )", categories.size)
                        
                        if (categories.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }
}