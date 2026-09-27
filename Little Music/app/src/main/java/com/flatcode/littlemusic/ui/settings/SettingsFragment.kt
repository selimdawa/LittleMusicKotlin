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
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.FragmentSettingsBinding
import com.flatcode.littlemusic.model.Setting
import com.flatcode.littlemusic.ui.album.MyAlbumsActivity
import com.flatcode.littlemusic.ui.artist.MyArtistsActivity
import com.flatcode.littlemusic.ui.category.MyCategoriesActivity
import com.flatcode.littlemusic.ui.favorites.FavoritesActivity
import com.flatcode.littlemusic.ui.profile.ProfileActivity
import com.flatcode.littlemusic.ui.profile.ProfileEditActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val list = ArrayList<Setting>()
    private var adapter: SettingAdapter? = null
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        Timber.d("SettingsFragment Created")

        initStaticSettings()
        adapter = SettingAdapter(list)
        binding.recyclerView.adapter = adapter

        setupToolbar()
        observeViewModel()

        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbar.item.setOnClickListener {
            context?.openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid))
        }
    }

    private fun initStaticSettings() {
        list.clear()
        list.add(
            Setting(
                DATA.EDIT_PROFILE,
                "Edit Profile",
                R.drawable.ic_edit_white,
                0,
                ProfileEditActivity::class.java
            )
        )
        list.add(Setting(DATA.MY_ALBUMS, "My Albums", R.drawable.ic_album, 0, MyAlbumsActivity::class.java))
        list.add(Setting(DATA.MY_ARTISTS, "My Artists", R.drawable.ic_mic, 0, MyArtistsActivity::class.java))
        list.add(
            Setting(
                DATA.MY_CATEGORIES,
                "My Categories",
                R.drawable.ic_category_gray,
                0,
                MyCategoriesActivity::class.java
            )
        )
        list.add(
            Setting(
                DATA.FAVORITES_ID,
                "Favorites",
                R.drawable.ic_star_selected,
                0,
                FavoritesActivity::class.java
            )
        )
        list.add(Setting(DATA.ABOUT_APP, "About App", R.drawable.ic_info, 0, null))
        list.add(Setting(DATA.LOGOUT, "Logout", R.drawable.ic_logout_white, 0, null))
        list.add(Setting(DATA.SHARE_APP, "Share App", R.drawable.ic_share, 0, null))
        list.add(Setting(DATA.RATE_APP, "Rate APP", R.drawable.ic_heart_selected, 0, null))
        list.add(
            Setting(
                DATA.PRIVACY_POLICY_ID,
                "Privacy Policy",
                R.drawable.ic_privacy_policy,
                0,
                PrivacyPolicyActivity::class.java
            )
        )
    }

    private fun updateSettingNumber(index: Int, count: Int) {
        if (index in list.indices && list[index].number != count) {
            list[index].number = count
            adapter?.notifyItemChanged(index)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collectLatest { user ->
                        user?.let {
                            binding.toolbar.imageProfile.loadImage(it.profileImage, true)
                            binding.toolbar.username.text = it.username
                            binding.toolbar.email.text = it.email
                        }
                    }
                }
                launch {
                    viewModel.itemCounts.collectLatest { counts ->
                        updateSettingNumber(1, counts[DATA.ALBUMS] ?: 0)
                        updateSettingNumber(2, counts[DATA.ARTISTS] ?: 0)
                        updateSettingNumber(3, counts[DATA.CATEGORIES] ?: 0)
                        updateSettingNumber(4, counts[DATA.FAVORITES] ?: 0)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
        viewModel.loadItemCounts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}