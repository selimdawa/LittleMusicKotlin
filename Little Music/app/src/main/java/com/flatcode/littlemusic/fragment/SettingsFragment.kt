package com.flatcode.littlemusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.adapter.SettingAdapter
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.FragmentSettingsBinding
import com.flatcode.littlemusic.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null
    private val viewModel: SettingsViewModel by viewModels()
    private var adapter: SettingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        Timber.d("SettingsFragment Created")

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        return binding!!.root
    }

    private fun setupToolbar() {
        binding!!.toolbar.item.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }
    }

    private fun setupRecyclerView() {
        adapter = SettingAdapter(context, ArrayList())
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { user ->
                        user?.let {
                            VOID.GlideImage(true, context, it.profileImage, binding!!.toolbar.imageProfile)
                            binding!!.toolbar.username.text = it.username
                            binding!!.toolbar.email.text = it.email
                        }
                    }
                }
                launch {
                    viewModel.settings.collect { settings ->
                        adapter?.list?.clear()
                        adapter?.list?.addAll(settings)
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
        viewModel.loadSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}