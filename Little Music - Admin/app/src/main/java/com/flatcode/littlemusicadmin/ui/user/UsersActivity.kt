package com.flatcode.littlemusicadmin.ui.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.flatcode.littlemusicadmin.utils.BaseActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.ActivityUsersBinding
import com.flatcode.littlemusicadmin.ui.profile.ProfileActivity
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class UsersActivity : BaseActivity() {

    private lateinit var binding: ActivityUsersBinding
    private var adapter: UserAdapter? = null
    private val viewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.users)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.toolbar.visibility = View.VISIBLE
                    binding.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                    viewModel.setSearchQuery("")
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

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

        adapter = UserAdapter { user ->
            openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to user.id))
        }
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener {
            viewModel.setOrderBy(DATA.TIMESTAMP)
        }
        binding.switchBar.name.setOnClickListener {
            viewModel.setOrderBy(DATA.NAME)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.users.collectLatest { users ->
                binding.toolbar.number.text = MessageFormat.format("( {0} )", users.size)
                adapter!!.submitList(users)

                if (users.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }
                Timber.d("Users updated: ${users.size}")
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }


}
