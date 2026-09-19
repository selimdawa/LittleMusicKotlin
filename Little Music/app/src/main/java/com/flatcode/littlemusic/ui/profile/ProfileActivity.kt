package com.flatcode.littlemusic.ui.profile

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.glideImage
import com.flatcode.littlemusic.utils.openActivity
import com.flatcode.littlemusic.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.i("ProfileActivity Created")

        ViewCompat.setOnApplyWindowInsetsListener(binding.edit.parent as View) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 20
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.numberAlbums.parent.parent as View) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom + 20)
            windowInsets
        }

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        if (profileId == DATA.FirebaseUserUid) {
            binding.edit.visibility = View.VISIBLE
            binding.edit.setImageResource(R.drawable.ic_edit_white)
            binding.edit.setOnClickListener { this.openActivity<ProfileEditActivity>() }
        }
        binding.back.setOnClickListener { onBackPressed() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.username.collect { username ->
                        binding.username.text = username
                    }
                }
                launch {
                    viewModel.profileImage.collect { profileImage ->
                        binding.profile.glideImage(profileImage, true)
                    }
                }
                launch {
                    viewModel.favoritesCount.collect { count ->
                        binding.numberFavorites.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.albumsCount.collect { count ->
                        binding.numberAlbums.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.artistsCount.collect { count ->
                        binding.numberArtists.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.categoriesCount.collect { count ->
                        binding.numberCategories.text = MessageFormat.format("{0}", count)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileId?.let {
            viewModel.loadUserInfo(it)
            viewModel.loadCounts(it)
        }
    }
}