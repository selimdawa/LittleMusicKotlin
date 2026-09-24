package com.flatcode.littlemusicadmin.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import java.text.MessageFormat

fun Category.moreDelete(
    activity: Activity, db: String?, idDb: String?,
    childDb: String?, db2: String?, idDb2: String?, childDb2: String?,
    db3: String?, idDb3: String?, childDb3: String?,
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
                db,
                idDb,
                childDb,
                db2,
                idDb2,
                childDb2,
                db3,
                idDb3,
                childDb3
            )
        }
    }.show()
}

fun Album.moreDelete(
    activity: Activity, db: String?, idDb: String?, childDb: String?,
    db2: String?, idDb2: String?, childDb2: String?,
    db3: String?, idDb3: String?, childDb3: String?,
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
                db,
                idDb,
                childDb,
                db2,
                idDb2,
                childDb2,
                db3,
                idDb3,
                childDb3
            )
        }
    }.show()
}

fun Artist.moreDelete(
    activity: Activity, db: String?, idDb: String?, childDb: String?,
    db2: String?, idDb2: String?, childDb2: String?,
    db3: String?, idDb3: String?, childDb3: String?,
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
                db,
                idDb,
                childDb,
                db2,
                idDb2,
                childDb2,
                db3,
                idDb3,
                childDb3
            )
        }
    }.show()
}

fun Song.moreDelete(
    activity: Activity, db: String?, idDb: String?, childDb: String?,
    db2: String?, idDb2: String?, childDb2: String?,
    db3: String?, idDb3: String?, childDb3: String?,
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
                db,
                idDb,
                childDb,
                db2,
                idDb2,
                childDb2,
                db3,
                idDb3,
                childDb3
            )
        }
    }.show()
}

fun Activity.dialogOptionDelete(
    id: String?, name: String, type: String?, nameDb: String?,
    isEditorsChoice: Boolean, db: String?, idDb: String?, childDb: String?,
    db2: String?, idDb2: String?, childDb2: String?,
    db3: String?, idDb3: String?, childDb3: String?,
) {
    if (isFinishing || isDestroyed) return

    val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.title.text =
        MessageFormat.format("Do you want to delete {0} ( {1} ) ?", name, type)

    dialogBinding.yes.setOnClickListener {
        if (isEditorsChoice) {
            this.dialogUpdateEditorsChoice(alertDialog, id)
        } else {
            this.deleteDb(
                alertDialog,
                id,
                name,
                nameDb,
                db,
                idDb,
                childDb,
                db2,
                idDb2,
                childDb2,
                db3,
                idDb3,
                childDb3
            )
        }
    }
    dialogBinding.no.setOnClickListener { alertDialog.dismiss() }

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun Context.dialogUpdateEditorsChoice(dialogDelete: Dialog, id: String?) {
    val dialog = AlertDialog.Builder(this).apply {
        setMessage("Updating Editors Choice...")
        setCancelable(false)
    }.show()
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

fun Activity.deleteDb(
    dialogDelete: Dialog, id: String?, name: String, nameDb: String?,
    db: String?, idDb: String?, childDb: String?, db2: String?, idDb2: String?,
    childDb2: String?, db3: String?, idDb3: String?, childDb3: String?,
) {
    val dialog = AlertDialog.Builder(this).apply {
        setTitle("Please wait")
        setMessage("Deleting $name ...")
        setCancelable(false)
    }.show()
    val reference = FirebaseDatabase.getInstance().getReference(nameDb!!)
    reference.child(id!!).removeValue().addOnSuccessListener {
        if ((db != null) && (idDb != null) && (childDb != null)) idDb.incrementItemRemoveCount(
            db, childDb
        )
        if ((db2 != null) && (idDb2 != null) && (childDb2 != null)) idDb2.incrementItemRemoveCount(
            db2, childDb2
        )
        if ((db3 != null) && (idDb3 != null) && (childDb3 != null)) idDb3.incrementItemRemoveCount(
            db3, childDb3
        )
        DATA.isChange = true
        (this as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
        dialog.dismiss()
        Toast.makeText(this, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
        dialogDelete.dismiss()
    }.addOnFailureListener { e: Exception ->
        dialog.dismiss()
        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.addToEditorsChoice(id: String?, number: Int) {
    val dialog = AlertDialog.Builder(this).apply {
        setMessage("Updating Editors Choice...")
        setCancelable(false)
    }.show()
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

fun Context.dialogAboutArtist(imageDb: String?, nameDb: String?, aboutDb: String?) {
    val activity = this as? Activity ?: return
    if (activity.isFinishing || activity.isDestroyed) return

    val dialogBinding = DialogAboutArtistBinding.inflate(activity.layoutInflater)
    val alertDialog = MaterialAlertDialogBuilder(this).setView(dialogBinding.root).create()

    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    dialogBinding.image.loadImage(true, imageDb)
    dialogBinding.name.text = MessageFormat.format("{0}{1}", DATA.EMPTY, nameDb)
    dialogBinding.aboutTheArtist.text = MessageFormat.format("{0}{1}", DATA.EMPTY, aboutDb)

    alertDialog.show()

    val widthPx = (300 * resources.displayMetrics.density).toInt()
    alertDialog.window?.setLayout(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)
}