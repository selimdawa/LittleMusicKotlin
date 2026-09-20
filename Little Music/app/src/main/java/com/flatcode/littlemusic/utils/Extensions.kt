package com.flatcode.littlemusic.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemusic.R
import com.flatcode.littlemusic.databinding.DialogAboutAppBinding
import com.flatcode.littlemusic.databinding.DialogAboutArtistBinding
import com.flatcode.littlemusic.databinding.DialogCloseAppBinding
import com.flatcode.littlemusic.databinding.DialogLogoutBinding
import com.flatcode.littlemusic.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.text.MessageFormat

inline fun <reified T : Activity> Context.openActivity(
    c: Class<*>? = null,
    clear: Boolean = false,
    vararg extras: Pair<String, Any?>
) {
    val target = c ?: T::class.java
    val intent = Intent(this, target).apply {
        if (clear) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Double -> putExtra(key, value)
                is Long -> putExtra(key, value)
                is Serializable -> putExtra(key, value)
            }
        }
    }
    startActivity(intent)
}

fun ImageView.glideImage(url: String?, isUser: Boolean = false) {
    try {
        if (url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_music)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                crossfade(true)
            }
        }
    } catch (e: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

fun ImageView.glideBlur(url: String?, level: Int, isUser: Boolean = false) {
    try {
        if (url == DATA.BASIC) {
            if (isUser) {
                this.setImageResource(R.drawable.basic_user)
            } else {
                this.setImageResource(R.drawable.basic_music)
            }
        } else {
            this.load(url) {
                placeholder(R.color.image_profile)
                transformations(SimpleBlurTransformation(level.toFloat()))
            }
        }
    } catch (e: Exception) {
        this.setImageResource(R.drawable.basic_music)
    }
}

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

fun Context.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
    shareIntent.putExtra(
        Intent.EXTRA_TEXT,
        " Download the app now from Google Play  https://play.google.com/store/apps/details?id=" + this.packageName
    )
    this.startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
}

fun Context.rateApp() {
    val uri = Uri.parse("market://details?id=" + this.packageName)
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        this.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)
            )
        )
    }
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
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + DATA.FB_ID))
        }
        this.startActivity(openFacebookIntent)
    }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
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

    binding.image.glideImage(imageDB, false)
    binding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
    binding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun Uri.getFileExtension(context: Context): String {
    val cR: ContentResolver = context.contentResolver
    val mime: MimeTypeMap = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this))!!
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(userId!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(id!!).exists()) {
                this@isFavorite.setImageResource(R.drawable.ic_star_selected)
                this@isFavorite.tag = "added"
            } else {
                this@isFavorite.setImageResource(R.drawable.ic_star_unselected)
                this@isFavorite.tag = "add"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun String.incrementLovesCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) lovesCount = "0"
            val newLovesCount = lovesCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.LOVES_COUNT] = newLovesCount
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementLovesRemoveCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child("lovesCount").value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val removeLovesCount = lovesCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap["lovesCount"] = removeLovesCount
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") {
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).setValue(true)
    } else {
        FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES)
            .child(DATA.FirebaseUserUid).child(id!!).removeValue()
    }
}

fun ImageView.checkInterested(type: String?, id: String?) {
    if (this.tag == "add") {
        id?.incrementInterestedCount(type)
        FirebaseDatabase.getInstance().reference.child(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).setValue(true)
    } else {
        id?.incrementInterestedRemoveCount(type)
        FirebaseDatabase.getInstance().reference.child(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(type!!).child(id!!).removeValue()
    }
}

fun ImageView.checkLove(id: String?) {
    if (this.tag == "love") {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).setValue(true)
        id.incrementLovesCount()
    } else {
        FirebaseDatabase.getInstance().getReference(DATA.LOVES).child(id!!)
            .child(DATA.FirebaseUserUid).removeValue()
        id.incrementLovesRemoveCount()
    }
}

fun ImageView.isLoves(id: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                this@isLoves.setImageResource(R.drawable.ic_heart_selected)
                this@isLoves.tag = "loved"
            } else {
                this@isLoves.setImageResource(R.drawable.ic_heart_unselected)
                this@isLoves.tag = "love"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun ImageView.isInterested(id: String?, type: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).child(DATA.FirebaseUserUid)
            .child(type!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.child(id!!).exists()) {
                this@isInterested.setImageResource(R.drawable.ic_star_selected)
                this@isInterested.tag = "added"
            } else {
                this@isInterested.setImageResource(R.drawable.ic_star_unselected)
                this@isInterested.tag = "add"
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun TextView.nrLoves(id: String?) {
    val reference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

fun String.incrementViewCount() {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).value
            if (viewsCount == DATA.EMPTY || viewsCount == DATA.NULL) {
                viewsCount = "0"
            }
            val newViewsCount = viewsCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.VIEWS_COUNT] = newViewsCount
            val ref2: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            ref2.child(this@incrementViewCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementInterestedCount(type: String?) {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).value
            if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                interestedCount = "0"
            }
            val newInterestedCount = interestedCount.toLong() + 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.INTERESTED_COUNT] = newInterestedCount
            val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(type)
            reference.child(this@incrementInterestedCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String.incrementInterestedRemoveCount(type: String?) {
    val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference(type!!)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var interestedCount = DATA.EMPTY + snapshot.child(DATA.INTERESTED_COUNT).value
            if (interestedCount == DATA.EMPTY || interestedCount == DATA.NULL) {
                interestedCount = "0"
            }
            val removeInterestedCount = interestedCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.INTERESTED_COUNT] = removeInterestedCount
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference(type)
            reference.child(this@incrementInterestedRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun TextView.dataName(database: String?, dataId: String?) {
    val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(database!!)
    reference.child(dataId!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val Name = DATA.EMPTY + snapshot.child(DATA.NAME).value
            this@dataName.text = MessageFormat.format("{0}{1}", DATA.EMPTY, Name)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun Long.getTimeAgo(): String? {
    var time = this
    if (time < 1000000000000L) {
        time *= 1000
    }
    val now = System.currentTimeMillis()
    if (time > now || time <= 0) return null

    val diff = now - time
    return when {
        diff < 60000 -> "just now"
        diff < 120000 -> "a minute ago"
        diff < 3000000 -> "${diff / 60000} minutes ago"
        diff < 5400000 -> "an hour ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        diff < 172800000 -> "yesterday"
        else -> "${diff / 86400000} days ago"
    }
}

fun Long.getMessageAgo(): String? {
    var time = this
    if (time < 1000000000000L) {
        time *= 1000
    }
    val now = System.currentTimeMillis()
    if (time > now || time <= 0) return null

    val diff = now - time
    return when {
        diff < 60000 -> "1 s"
        diff < 120000 -> "1 m"
        diff < 3000000 -> "${diff / 60000} m"
        diff < 5400000 -> "1 h"
        diff < 86400000 -> "${diff / 3600000} h"
        diff < 172800000 -> "1 d"
        else -> "${diff / 86400000} d"
    }
}

class SimpleBlurTransformation(private val radius: Float) : Transformation() {
    override val cacheKey: String = "${SimpleBlurTransformation::class.java.name}-$radius"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        if (input.isRecycled) return input
        val scaleFactor = 6
        val w = (input.width / scaleFactor).coerceAtLeast(1)
        val h = (input.height / scaleFactor).coerceAtLeast(1)
        val small = input.scale(w, h, true)
        val r = (radius / scaleFactor).toInt().coerceAtLeast(1)
        val pix = IntArray(w * h)
        small.getPixels(pix, 0, w, 0, 0, w, h)
        val blurred = IntArray(w * h)
        for (y in 0 until h) for (x in 0 until w) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val xi = (x + i).coerceIn(0, w - 1)
                val p = pix[y * w + xi]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            blurred[y * w + x] = (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        for (x in 0 until w) for (y in 0 until h) {
            var rs = 0L
            var gs = 0L
            var bs = 0L
            var c = 0
            for (i in -r..r) {
                val yi = (y + i).coerceIn(0, h - 1)
                val p = blurred[yi * w + x]
                rs += (p shr 16) and 0xff
                gs += (p shr 8) and 0xff
                bs += p and 0xff
                c++
            }
            pix[y * w + x] = (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(pix, 0, w, 0, 0, w, h)
        val finalOutput = output.scale(input.width, input.height, true)
        if (output != finalOutput) output.recycle()
        if (small != input) small.recycle()
        return finalOutput
    }
}