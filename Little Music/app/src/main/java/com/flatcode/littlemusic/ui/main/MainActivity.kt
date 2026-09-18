package com.flatcode.littlemusic.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import io.selimdawa.bubblebottom.Model
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var bottomNavigation: BubbleBottomNavigation? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.toolbar.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 10 // adding original margin
            }
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.bottomNavigation) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = insets.bottom)
            windowInsets
        }

        Timber.i("MainActivity Created")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigation = binding!!.bottomNavigation
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

            binding!!.toolbar.card.visibility = if (destinationId == R.id.homeFragment) View.VISIBLE else View.GONE

            if (navController.currentDestination?.id != destinationId) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId, false)
                    .setLaunchSingleTop(true)
                    .build()
                navController.navigate(destinationId, null, navOptions)
            }
        }

        //bottomNavigation.setCount(3, numberSongs);
        bottomNavigation!!.show(2, true)

        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(context, CLASS.PROFILE, DATA.PROFILE_ID, DATA.FirebaseUserUid)
        }

        observeViewModel()
        viewModel.loadUserInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileImage.collect { profileImage ->
                    profileImage?.let {
                        VOID.GlideImage(true, context, it, binding!!.toolbar.image)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        VOID.closeApp(context, activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}