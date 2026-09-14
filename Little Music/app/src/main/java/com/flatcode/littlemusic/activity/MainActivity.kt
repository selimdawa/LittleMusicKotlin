package com.flatcode.littlemusic.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemusic.fragment.SettingsFragment
import com.flatcode.littlemusic.fragment.mySongsFragment
import com.flatcode.littlemusic.fragment.CategoriesFragment
import com.flatcode.littlemusic.fragment.HomeFragment
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityMainBinding
import com.flatcode.littlemusic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Objects

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var bottomNavigation: BubbleBottomNavigation? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        Timber.i("MainActivity Created")

        bottomNavigation = binding!!.bottomNavigation
        bottomNavigation!!.add(BubbleBottomNavigation.Model(1, R.drawable.ic_settings))
        bottomNavigation!!.add(BubbleBottomNavigation.Model(2, R.drawable.ic_home))
        bottomNavigation!!.add(BubbleBottomNavigation.Model(3, R.drawable.ic_books))
        bottomNavigation!!.add(BubbleBottomNavigation.Model(4, R.drawable.ic_group))
        bottomNavigation!!.setOnShowListener { item: BubbleBottomNavigation.Model ->
            var fragment: Fragment? = null
            when (item.id) {
                1 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = SettingsFragment()
                }

                2 -> {
                    binding!!.toolbar.card.visibility = View.VISIBLE
                    fragment = HomeFragment()
                }

                3 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = mySongsFragment()
                }

                4 -> {
                    binding!!.toolbar.card.visibility = View.GONE
                    fragment = CategoriesFragment()
                }
            }
            loadFragment(fragment)
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

    private fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment!!)
            .commit()
    }

    override fun onBackPressed() {
        VOID.closeApp(context, activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
    }
}