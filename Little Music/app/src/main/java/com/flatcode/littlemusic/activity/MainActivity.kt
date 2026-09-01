package com.flatcode.littlemusic.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flatcode.littlemusic.fragment.SettingsFragment
import com.flatcode.littlemusic.fragment.mySongsFragment
import com.flatcode.littlemusic.fragment.CategoriesFragment
import com.flatcode.littlemusic.fragment.HomeFragment
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.utils.VOID
import com.flatcode.littlemusic.utils.CLASS
import com.flatcode.littlemusic.utils.DATA
import com.flatcode.littlemusic.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import java.util.Objects

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var bottomNavigation: BubbleBottomNavigation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

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
        loadUserInfo()
    }

    private fun loadFragment(fragment: Fragment?) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment!!)
            .commit()
    }

    private fun loadUserInfo() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        reference.child(Objects.requireNonNull(DATA.FirebaseUserUid))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value
                    VOID.GlideImage(true, context, profileImage, binding!!.toolbar.image)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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