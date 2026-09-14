package com.flatcode.littlemusic.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityProfileBinding
import com.flatcode.littlemusic.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private var profileId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        Timber.i("ProfileActivity Created")

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        if (profileId == DATA.FirebaseUserUid) {
            binding!!.edit.visibility = View.VISIBLE
            binding!!.edit.setImageResource(R.drawable.ic_edit_white)
            binding!!.edit.setOnClickListener { VOID.Intent1(this, CLASS.PROFILE_EDIT) }
        }
        binding!!.back.setOnClickListener { onBackPressed() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.username.collect { username ->
                        binding!!.username.text = username
                    }
                }
                launch {
                    viewModel.profileImage.collect { profileImage ->
                        VOID.GlideImage(true, this@ProfileActivity, profileImage, binding!!.profile)
                    }
                }
                launch {
                    viewModel.favoritesCount.collect { count ->
                        binding!!.numberFavorites.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.albumsCount.collect { count ->
                        binding!!.numberAlbums.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.artistsCount.collect { count ->
                        binding!!.numberArtists.text = MessageFormat.format("{0}", count)
                    }
                }
                launch {
                    viewModel.categoriesCount.collect { count ->
                        binding!!.numberCategories.text = MessageFormat.format("{0}", count)
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