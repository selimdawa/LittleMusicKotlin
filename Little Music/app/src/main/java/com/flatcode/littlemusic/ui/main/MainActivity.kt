package com.flatcode.littlemusic.ui.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.ActivityMainBinding
import com.flatcode.littlemusic.ui.profile.ProfileActivity
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.utils.closeApp
import com.flatcode.littlemusic.utils.loadImage
import com.flatcode.littlemusic.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import io.selimdawa.bubblebottom.Model
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var bottomNavigation: BubbleBottomNavigation? = null
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (!granted) {
            Toast.makeText(
                this, "Permissions are required for better experience", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            requestPermissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 10 // adding original margin
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        Timber.i("MainActivity Created")

        checkPermissions()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigation = binding.bottomNavigation
        bottomNavigation!!.add(Model(1, R.drawable.ic_settings))
        bottomNavigation!!.add(Model(2, R.drawable.ic_home))
        bottomNavigation!!.add(Model(3, R.drawable.ic_books))
        bottomNavigation!!.add(Model(4, R.drawable.ic_group))
        bottomNavigation!!.setOnShowListener { item: Model ->
            val destinationId = when (item.id) {
                1 -> R.id.settingsFragment
                2 -> R.id.homeFragment
                3 -> R.id.mySongsFragment
                4 -> R.id.categoriesFragment
                else -> R.id.homeFragment
            }

            binding.toolbar.card.visibility =
                if (destinationId == R.id.homeFragment) View.VISIBLE else View.GONE

            if (navController.currentDestination?.id != destinationId) {
                val navOptions =
                    NavOptions.Builder().setPopUpTo(navController.graph.startDestinationId, false)
                        .setLaunchSingleTop(true).build()
                navController.navigate(destinationId, null, navOptions)
            }
        }

        bottomNavigation!!.show(2, true)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                context.closeApp(activity)
            }
        })

        context.openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid))

        observeViewModel()
        viewModel.loadUserInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileImage.collect { profileImage ->
                    profileImage?.let {
                        binding.toolbar.image.loadImage(it, true)
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}