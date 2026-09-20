package com.flatcode.littlemusicadmin.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.Transformation
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.databinding.DialogAboutArtistBinding
import com.flatcode.littlemusicadmin.databinding.DialogLogoutBinding
import com.flatcode.littlemusicadmin.model.Album
import com.flatcode.littlemusicadmin.model.Artist
import com.flatcode.littlemusicadmin.model.Category
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.ui.album.AlbumEditActivity
import com.flatcode.littlemusicadmin.ui.artist.ArtistEditActivity
import com.flatcode.littlemusicadmin.ui.category.CategoryEditActivity
import com.flatcode.littlemusicadmin.ui.song.SongEditActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import java.io.Serializable
import java.text.MessageFormat
import java.util.Locale

inline fun <reified T : Activity> Context.openActivity(
    clear: Boolean = false, vararg extras: Pair<String, Any?>
) {
    val intent = Intent(this, T::class.java).apply {
        if (clear) addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        extras.forEach { (key, value) ->
            when (value) {
                is String -> putExtra(key, value)
                is Int -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Long -> putExtra(key, value)
                is Double -> putExtra(key, value)
                is Float -> putExtra(key, value)
                is Serializable -> putExtra(key, value)
                is Bundle -> putExtra(key, value)
                is Parcelable -> putExtra(key, value)
            }
        }
    }
    startActivity(intent)
}

fun ImageView.glide(isUser: Boolean, url: String?) {
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

fun ImageView.glideBlur(isUser: Boolean, url: String?, level: Int) {
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

fun String?.incrementViewCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var viewsCount = DATA.EMPTY + snapshot.child(DATA.VIEWS_COUNT).value
            if (viewsCount == DATA.EMPTY || viewsCount == DATA.NULL) {
                viewsCount = "0"
            }
            val newViewsCount = viewsCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[DATA.VIEWS_COUNT] = newViewsCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementViewCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementLovesCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val newLovesCount = lovesCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[DATA.LOVES_COUNT] = newLovesCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementLovesRemoveCount() {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(DATA.LOVES_COUNT).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) {
                lovesCount = "0"
            }
            val removeLovesCount = lovesCount.toLong() - 1
            val hashMap = HashMap<String, Any>()
            hashMap[DATA.LOVES_COUNT] = removeLovesCount
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
            reference.child(this@incrementLovesRemoveCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementItemCount(database: String, childDB: String) {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(database)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var itemsCount = DATA.EMPTY + snapshot.child(childDB).value
            if (itemsCount == DATA.EMPTY || itemsCount == DATA.NULL) {
                itemsCount = DATA.EMPTY + DATA.ZERO
            }
            val newItemsCount = itemsCount.toLong() + 1
            val hashMap = HashMap<String?, Any>()
            hashMap[childDB] = newItemsCount
            val reference = FirebaseDatabase.getInstance().getReference(database)
            reference.child(this@incrementItemCount).updateChildren(hashMap)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun String?.incrementItemRemoveCount(database: String, childDB: String) {
    if (this == null) return
    val ref = FirebaseDatabase.getInstance().getReference(database)
    ref.child(this).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var lovesCount = DATA.EMPTY + snapshot.child(childDB).value
            if (lovesCount == DATA.EMPTY || lovesCount == DATA.NULL) lovesCount =
                DATA.EMPTY + DATA.ZERO

            val i = lovesCount.toInt()
            if (i > 0) {
                val removeLovesCount = lovesCount.toInt() - 1
                val hashMap = HashMap<String?, Any>()
                hashMap[childDB] = removeLovesCount

                val reference = FirebaseDatabase.getInstance().getReference(database)
                reference.child(this@incrementItemRemoveCount).updateChildren(hashMap)
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun ImageView.isFavorite(id: String?, userId: String?) {
    val ref = FirebaseDatabase.getInstance().reference.child(DATA.FAVORITES).child(userId!!)
    ref.addValueEventListener(object : ValueEventListener {
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

fun ImageView.checkFavorite(id: String?) {
    if (this.tag == "add") FirebaseDatabase.getInstance().getReference(DATA.FAVORITES)
        .child(DATA.FirebaseUserUid).child(id!!).setValue(true) else FirebaseDatabase.getInstance()
        .getReference(DATA.FAVORITES).child(DATA.FirebaseUserUid).child(id!!).removeValue()
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
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
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

fun TextView.nrLoves(id: String?) {
    val reference = FirebaseDatabase.getInstance().reference.child(DATA.LOVES).child(id!!)
    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            this@nrLoves.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}

// Crop Image functions moved to Activity Result API in activities

fun Uri.getFileExtension(context: Context): String? {
    val cR = context.contentResolver
    val mime = MimeTypeMap.getSingleton()
    return mime.getExtensionFromMimeType(cR.getType(this))
}

fun Category.moreDelete(
    activity: Activity, DB: String?, idDB: String?,
    childDB: String?, DB2: String?, idDB2: String?, childDB2: String?,
    DB3: String?, idDB3: String?, childDB3: String?,
) {
    val id = DATA.EMPTY + this.id
    val name = DATA.EMPTY + this.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle("Choose Options").setItems(options) { _, which ->
            if (which == 0) {
                activity.openActivity<CategoryEditActivity>(extras = arrayOf(DATA.CATEGORY_ID to id))
            } else if (which == 1) {
                activity.dialogOptionDelete(
                    id,
                    name,
                    DATA.CATEGORY,
                    DATA.CATEGORIES,
                    false,
                    DB,
                    idDB,
                    childDB,
                    DB2,
                    idDB2,
                    childDB2,
                    DB3,
                    idDB3,
                    childDB3
                )
            }
        }.show()
}

fun Album.moreDelete(
    activity: Activity, DB: String?, idDB: String?, childDB: String?,
    DB2: String?, idDB2: String?, childDB2: String?,
    DB3: String?, idDB3: String?, childDB3: String?,
) {
    val id = DATA.EMPTY + this.id
    val category = DATA.EMPTY + this.categoryId
    val artist = DATA.EMPTY + this.artistId
    val name = DATA.EMPTY + this.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle("Choose Options").setItems(options) { _, which ->
            if (which == 0) {
                activity.openActivity<AlbumEditActivity>(
                    extras = arrayOf(
                        DATA.ALBUM_ID to id, DATA.CATEGORY_ID to category, DATA.ARTIST_ID to artist
                    )
                )
            } else if (which == 1) {
                activity.dialogOptionDelete(
                    id,
                    name,
                    DATA.ALBUM,
                    DATA.ALBUMS,
                    false,
                    DB,
                    idDB,
                    childDB,
                    DB2,
                    idDB2,
                    childDB2,
                    DB3,
                    idDB3,
                    childDB3
                )
            }
        }.show()
}

fun Artist.moreDelete(
    activity: Activity, DB: String?, idDB: String?, childDB: String?,
    DB2: String?, idDB2: String?, childDB2: String?,
    DB3: String?, idDB3: String?, childDB3: String?,
) {
    val id = DATA.EMPTY + this.id
    val name = DATA.EMPTY + this.name
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle("Choose Options").setItems(options) { _, which ->
            if (which == 0) {
                activity.openActivity<ArtistEditActivity>(extras = arrayOf(DATA.ARTIST_ID to id))
            } else if (which == 1) {
                activity.dialogOptionDelete(
                    id,
                    name,
                    DATA.ARTIST,
                    DATA.ARTISTS,
                    false,
                    DB,
                    idDB,
                    childDB,
                    DB2,
                    idDB2,
                    childDB2,
                    DB3,
                    idDB3,
                    childDB3
                )
            }
        }.show()
}

fun Song.moreDelete(
    activity: Activity, DB: String?, idDB: String?, childDB: String?,
    DB2: String?, idDB2: String?, childDB2: String?,
    DB3: String?, idDB3: String?, childDB3: String?,
) {
    val id = DATA.EMPTY + this.id
    val name = DATA.EMPTY + this.name
    val category = DATA.EMPTY + this.categoryId
    val artist = DATA.EMPTY + this.artistId
    val album = DATA.EMPTY + this.albumId
    val options = arrayOf("Edit", "Delete")
    val builder = AlertDialog.Builder(activity)
    builder.setTitle("Choose Options").setItems(options) { _, which ->
            if (which == 0) {
                activity.openActivity<SongEditActivity>(
                    extras = arrayOf(
                        DATA.SONG_ID to id,
                        DATA.CATEGORY_ID to category,
                        DATA.ARTIST_ID to artist,
                        DATA.ALBUM_ID to album
                    )
                )
            } else if (which == 1) {
                activity.dialogOptionDelete(
                    id,
                    name,
                    DATA.SONG,
                    DATA.SONGS,
                    false,
                    DB,
                    idDB,
                    childDB,
                    DB2,
                    idDB2,
                    childDB2,
                    DB3,
                    idDB3,
                    childDB3
                )
            }
        }.show()
}

fun Activity.dialogOptionDelete(
    id: String?, name: String, type: String?, nameDB: String?,
    isEditorsChoice: Boolean, DB: String?, idDB: String?, childDB: String?,
    DB2: String?, idDB2: String?, childDB2: String?,
    DB3: String?, idDB3: String?, childDB3: String?,
) {
    val binding = DialogLogoutBinding.inflate(LayoutInflater.from(this))
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
    binding.title.text = MessageFormat.format("Do you want to delete {0} ( {1} ) ?", name, type)

    binding.yes.setOnClickListener {
        if (isEditorsChoice) {
            this.dialogUpdateEditorsChoice(dialog, id)
        } else {
            this.deleteDB(
                dialog,
                id,
                name,
                nameDB,
                DB,
                idDB,
                childDB,
                DB2,
                idDB2,
                childDB2,
                DB3,
                idDB3,
                childDB3
            )
        }
    }
    binding.no.setOnClickListener { dialog.dismiss() }
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Context.dialogUpdateEditorsChoice(dialogDelete: Dialog, id: String?) {
    val dialog = ProgressDialog(this)
    dialog.setMessage("Updating Editors Choice...")
    dialog.show()
    val hashMap = HashMap<String?, Any>()
    hashMap[DATA.EDITORS_CHOICE] = 0
    val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
        dialog.dismiss()
        Toast.makeText(this, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }
}

fun Activity.deleteDB(
    dialogDelete: Dialog, id: String?, name: String, nameDB: String?,
    DB: String?, idDB: String?, childDB: String?, DB2: String?, idDB2: String?,
    childDB2: String?, DB3: String?, idDB3: String?, childDB3: String?,
) {
    val dialog = ProgressDialog(this)
    dialog.setTitle("Please wait")
    dialog.setMessage("Deleting $name ...")
    dialog.show()
    val reference = FirebaseDatabase.getInstance().getReference(nameDB!!)
    reference.child(id!!).removeValue().addOnSuccessListener {
        if ((DB != null) && (idDB != null) && (childDB != null)) idDB.incrementItemRemoveCount(
            DB,
            childDB
        )
        if ((DB2 != null) && (idDB2 != null) && (childDB2 != null)) idDB2.incrementItemRemoveCount(
            DB2,
            childDB2
        )
        if ((DB3 != null) && (idDB3 != null) && (childDB3 != null)) idDB3.incrementItemRemoveCount(
            DB3,
            childDB3
        )
        DATA.isChange = true
        this.onBackPressed()
        dialog.dismiss()
        Toast.makeText(this, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.addToEditorsChoice(id: String?, number: Int) {
    val dialog = ProgressDialog(this)
    dialog.setMessage("Updating Editors Choice...")
    dialog.show()
    val hashMap = HashMap<String?, Any>()
    hashMap[DATA.EDITORS_CHOICE] = number
    val reference = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
    reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
        dialog.dismiss()
        Toast.makeText(this, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
        this.finish()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun TextView.dataName(database: String?, dataId: String?) {
    val reference = FirebaseDatabase.getInstance().getReference(database!!)
    reference.child(dataId!!).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val nameValue = DATA.EMPTY + snapshot.child(DATA.NAME).value
            this@dataName.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameValue)
        }

        override fun onCancelled(error: DatabaseError) {}
    })
}

fun Context.dialogAboutArtist(imageDB: String?, nameDB: String?, aboutDB: String?) {
    val binding = DialogAboutArtistBinding.inflate(LayoutInflater.from(this))
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(binding.root)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val lp = WindowManager.LayoutParams()
    lp.copyFrom(dialog.window!!.attributes)
    lp.width = WindowManager.LayoutParams.WRAP_CONTENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    binding.image.glide(true, imageDB)
    binding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDB)
    binding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDB)
    dialog.show()
    dialog.window!!.attributes = lp
}

fun Long.convertDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
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
            blurred[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
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
            pix[y * w + x] =
                (0xff shl 24) or ((rs / c).toInt() shl 16) or ((gs / c).toInt() shl 8) or (bs / c).toInt()
        }
        val output = createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(pix, 0, w, 0, 0, w, h)
        val finalOutput = output.scale(input.width, input.height, true)
        if (output != finalOutput) output.recycle()
        if (small != input) small.recycle()
        return finalOutput
    }
}