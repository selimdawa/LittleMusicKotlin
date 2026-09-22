package com.flatcode.littlemusic.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.flatcode.littlemusic.databinding.DialogAboutAppBinding
import com.flatcode.littlemusic.databinding.DialogAboutArtistBinding
import com.flatcode.littlemusic.databinding.DialogCloseAppBinding
import com.flatcode.littlemusic.databinding.DialogLogoutBinding
import com.flatcode.littlemusic.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.MessageFormat

fun Context.closeApp(a: Activity?) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogCloseAppBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.yes.setOnClickListener { a!!.finish() }
    binding.no.setOnClickListener { dialog.cancel() }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogLogout() {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogLogoutBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.yes.setOnClickListener {
        FirebaseAuth.getInstance().signOut()
        this.openActivity<AuthActivity>(clear = true)
    }
    binding.no.setOnClickListener { dialog.cancel() }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogAboutApp() {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogAboutAppBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.website.setOnClickListener {
        this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DATA.WEB_SITE)))
    }
    binding.facebook.setOnClickListener {
        val openFacebookIntent = try {
            this.packageManager.getPackageInfo("com.facebook.katana", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + DATA.FB_ID))
        } catch (_: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + DATA.FB_ID))
        }
        this.startActivity(openFacebookIntent)
    }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogAboutArtist(imageDb: String?, nameDb: String?, aboutDb: String?) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    val binding = DialogAboutArtistBinding.inflate(LayoutInflater.from(this))
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    binding.image.loadImage(imageDb, false)
    binding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDb)
    binding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDb)
    dialog.show()
    dialog.window!!.attributes = lp
}