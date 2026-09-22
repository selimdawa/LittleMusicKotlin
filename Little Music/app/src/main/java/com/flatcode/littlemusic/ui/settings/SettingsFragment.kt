package com.flatcode.littlemusic.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.app.Activity
import com.flatcode.littlemusic.ui.profile.ProfileActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.utils.dialogAboutApp
import com.flatcode.littlemusic.utils.dialogLogout
import com.flatcode.littlemusic.utils.rateApp
import com.flatcode.littlemusic.utils.shareApp
import com.flatcode.littlemusic.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private var adapter: SettingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        Timber.d("SettingsFragment Created")

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupToolbar() {
            context?.openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid))
    }

    private fun setupRecyclerView() {
        adapter = SettingAdapter { item ->
            val context = context ?: return@SettingAdapter
            when (item.id) {
                "6" -> context.dialogAboutApp()
                "7" -> context.dialogLogout()
                "8" -> context.shareApp()
                "9" -> context.rateApp()
                else -> context.openActivity<Activity>(c = item.c)
            }
        }
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { user ->
                        user?.let {
                            binding.toolbar.imageProfile.loadImage(it.profileImage, true)
                            binding.toolbar.username.text = it.username
                            binding.toolbar.email.text = it.email
                        }
                    }
                }
                launch {
                    viewModel.settings.collect { settings ->
                        adapter?.submitList(settings)
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
        _binding = null
    }
}