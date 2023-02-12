package com.flatcode.littlemusic.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.flatcode.littlemusic.Fragment.SettingsFragment
import com.flatcode.littlemusic.Fragment.mySongsFragment
import com.flatcode.littlemusic.Fragmentimport.CategoriesFragment
import com.flatcode.littlemusic.Fragmentimport.HomeFragment
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.Unit.VOID
import com.flatcode.littlemusic.Unitimport.CLASSv
import com.flatcode.littlemusic.Unitimport.DATAv
import com.flatcode.littlemusic.Unitimport.THEME
import com.flatcode.littlemusic.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import meow.bottomnavigation.MeowBottomNavigation
import java.util.*

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    private var binding: ActivityMainBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var meowBottomNavigation: MeowBottomNavigation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.getDefaultSharedPreferences(baseContext)
            .registerOnSharedPreferenceChangeListener(this)
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        // Color Mode ----------------------------- Start
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingFragment())
            .commit()
        // Color Mode -------------------------------- End

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        if (sharedPreferences.getString(DATAv.COLOR_OPTION, "ONE") == "ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.sun)
        } else if (sharedPreferences.getString(DATAv.COLOR_OPTION, "NIGHT_ONE") == "NIGHT_ONE") {
            binding!!.toolbar.mode.setBackgroundResource(R.drawable.moon)
        }
        meowBottomNavigation = binding!!.bottomNavigation
        meowBottomNavigation!!.add(MeowBottomNavigation.Model(1, R.drawable.ic_settings))
        meowBottomNavigation!!.add(MeowBottomNavigation.Model(2, R.drawable.ic_home))
        meowBottomNavigation!!.add(MeowBottomNavigation.Model(3, R.drawable.ic_books))
        meowBottomNavigation!!.add(MeowBottomNavigation.Model(4, R.drawable.ic_group))
        meowBottomNavigation!!.setOnShowListener { item: MeowBottomNavigation.Model ->
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

        //meowBottomNavigation.setCount(3, numberSongs);
        meowBottomNavigation!!.show(2, true)
        meowBottomNavigation!!.setOnClickMenuListener { item: MeowBottomNavigation.Model ->
            when (item.id) {
                1 -> Toast.makeText(
                    applicationContext, R.string.settings, Toast.LENGTH_SHORT
                ).show()
                2 -> Toast.makeText(applicationContext, R.string.home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, R.string.my_songs, Toast.LENGTH_SHORT)
                    .show()
                4 -> Toast.makeText(applicationContext, R.string.categories, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        meowBottomNavigation!!.setOnReselectListener { item: MeowBottomNavigation.Model ->
            when (item.id) {
                1 -> Toast.makeText(
                    applicationContext, R.string.settings, Toast.LENGTH_SHORT
                ).show()
                2 -> Toast.makeText(applicationContext, R.string.home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, R.string.my_songs, Toast.LENGTH_SHORT)
                    .show()
                4 -> Toast.makeText(applicationContext, R.string.categories, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding!!.toolbar.image.setOnClickListener {
            VOID.IntentExtra(
                context,
                CLASSv.PROFILE,
                DATAv.PROFILE_ID,
                DATAv.FirebaseUserUid
            )
        }
        loadUserInfo()
    }

    private fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainer,
            fragment!!
        ).commit()
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.USERS)
        reference.child(Objects.requireNonNull(DATAv.FirebaseUserUid))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImage = DATAv.EMPTY + snapshot.child(DATAv.PROFILE_IMAGE).value
                    VOID.GlideImage(true, context, profileImage, binding!!.toolbar.image)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onBackPressed() {
        VOID.closeApp(context, activity)
    }

    // Color Mode ----------------------------- Start
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == DATAv.COLOR_OPTION) {
            recreate()
        }
    }

    class SettingFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_CODE) {
            recreate()
        }
    } // Color Mode -------------------------------- End

    companion object {
        private const val SETTINGS_CODE = 234
    }
}