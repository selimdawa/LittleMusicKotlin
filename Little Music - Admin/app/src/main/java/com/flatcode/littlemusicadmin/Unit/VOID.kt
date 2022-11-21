package com.flatcode.littlemusicadmin.Unit

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flatcode.littlemusicadmin.Model.Album
import com.flatcode.littlemusicadmin.Model.Artist
import com.flatcode.littlemusicadmin.Model.Category
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import jp.wasabeef.glide.transformations.BlurTransformation
import java.text.MessageFormat

object VOID {
    fun IntentClear(context: Context, c: Class<*>?) {
        val intent = Intent(context, c)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun Intent1(context: Context, c: Class<*>?) {
        val intent = Intent(context, c)
        context.startActivity(intent)
    }

    fun IntentExtra(context: Context, c: Class<*>?, key: String?, value: String?) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        context.startActivity(intent)
    }

    fun IntentExtra2(
        context: Context,
        c: Class<*>?,
        key: String?,
        value: String?,
        key2: String?,
        value2: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        context.startActivity(intent)
    }

    fun IntentExtra3(
        context: Context,
        c: Class<*>?,
        key: String?,
        value: String?,
        key2: String?,
        value2: String?,
        key3: String?,
        value3: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        intent.putExtra(key3, value3)
        context.startActivity(intent)
    }

    fun IntentExtra4(
        context: Context,
        c: Class<*>?,
        key: String?,
        value: String?,
        key2: String?,
        value2: String?,
        key3: String?,
        value3: String?,
        key4: String?,
        value4: String?,
    ) {
        val intent = Intent(context, c)
        intent.putExtra(key, value)
        intent.putExtra(key2, value2)
        intent.putExtra(key3, value3)
        intent.putExtra(key4, value4)
        context.startActivity(intent)
    }

    fun Glide(isUser: Boolean, context: Context?, Url: String?, Image: ImageView) {
        try {
            if (Url == DATAv.BASIC) {
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user)
                } else {
                    Image.setImageResource(R.drawable.basic_music)
                }
            } else {
                Glide.with(context!!).load(Url).placeholder(R.color.image_profile).into(Image)
            }
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun GlideBlur(isUser: Boolean, context: Context?, Url: String?, Image: ImageView, level: Int) {
        try {
            if (Url == DATAv.BASIC) {
                if (isUser) {
                    Image.setImageResource(R.drawable.basic_user)
                } else {
                    Image.setImageResource(R.drawable.basic_music)
                }
            } else {
                Glide.with(context!!).load(Url).placeholder(R.color.image_profile)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(level))).into(Image)
            }
        } catch (e: Exception) {
            Image.setImageResource(R.drawable.basic_music)
        }
    }

    fun incrementViewCount(id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var viewsCount = DATAv.EMPTY + snapshot.child(DATAv.VIEWS_COUNT).value
                if (viewsCount == DATAv.EMPTY || viewsCount == DATAv.NULL) {
                    viewsCount = "0"
                }
                val newViewsCount = viewsCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATAv.VIEWS_COUNT] = newViewsCount
                val reference = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementLovesCount(id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATAv.EMPTY + snapshot.child(DATAv.LOVES_COUNT).value
                if (lovesCount == DATAv.EMPTY || lovesCount == DATAv.NULL) {
                    lovesCount = "0"
                }
                val newLovesCount = lovesCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[DATAv.LOVES_COUNT] = newLovesCount
                hashMap[DATAv.LOVES_COUNT] = newLovesCount
                val reference = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementLovesRemoveCount(id: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATAv.EMPTY + snapshot.child("lovesCount").value
                if (lovesCount == DATAv.EMPTY || lovesCount == DATAv.NULL) {
                    lovesCount = "0"
                }
                val removeLovesCount = lovesCount.toLong() - 1
                val hashMap = HashMap<String, Any>()
                hashMap["lovesCount"] = removeLovesCount
                val reference = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementItemCount(database: String?, id: String?, childDB: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var itemsCount = DATAv.EMPTY + snapshot.child(childDB!!).value
                if (itemsCount == DATAv.EMPTY || itemsCount == DATAv.NULL) {
                    itemsCount = DATAv.EMPTY + DATAv.ZERO
                }
                val newItemsCount = itemsCount.toLong() + 1
                val hashMap = HashMap<String?, Any>()
                hashMap[childDB] = newItemsCount
                val reference = FirebaseDatabase.getInstance().getReference(database)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementItemRemoveCount(database: String?, id: String?, childDB: String?) {
        val ref = FirebaseDatabase.getInstance().getReference(database!!)
        ref.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //get views count
                var lovesCount = DATAv.EMPTY + snapshot.child(childDB!!).value
                if (lovesCount == DATAv.EMPTY || lovesCount == DATAv.NULL) {
                    lovesCount = DATAv.EMPTY + DATAv.ZERO
                }
                val removeLovesCount = lovesCount.toLong() - 1
                val hashMap = HashMap<String?, Any>()
                hashMap[childDB] = removeLovesCount
                val reference = FirebaseDatabase.getInstance().getReference(database)
                reference.child(id).updateChildren(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun isFavorite(add: ImageView, Id: String?, UserId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATAv.FAVORITES).child(
            UserId!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(Id!!).exists()) {
                    add.setImageResource(R.drawable.ic_star_selected)
                    add.tag = "added"
                } else {
                    add.setImageResource(R.drawable.ic_star_unselected)
                    add.tag = "add"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun checkFavorite(image: ImageView, id: String?) {
        if (image.tag == "add") FirebaseDatabase.getInstance().getReference(DATAv.FAVORITES)
            .child(DATAv.FirebaseUserUid)
            .child(id!!).setValue(true) else FirebaseDatabase.getInstance()
            .getReference(DATAv.FAVORITES).child(DATAv.FirebaseUserUid)
            .child(id!!).removeValue()
    }

    fun checkLove(image: ImageView, id: String?) {
        if (image.tag == "love") {
            FirebaseDatabase.getInstance().getReference(DATAv.LOVES).child(id!!)
                .child(DATAv.FirebaseUserUid).setValue(true)
            incrementLovesCount(id)
        } else {
            FirebaseDatabase.getInstance().getReference(DATAv.LOVES).child(id!!)
                .child(DATAv.FirebaseUserUid).removeValue()
            incrementLovesRemoveCount(id)
        }
    }

    fun isLoves(image: ImageView, id: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATAv.LOVES).child(
            id!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(DATAv.FirebaseUserUid).exists()) {
                    image.setImageResource(R.drawable.ic_heart_selected)
                    image.tag = "loved"
                } else {
                    image.setImageResource(R.drawable.ic_heart_unselected)
                    image.tag = "love"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun nrLoves(number: TextView, id: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATAv.LOVES).child(
            id!!
        )
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                number.text = MessageFormat.format(" {0} ", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun CropImageSquare(activity: Activity?) {
        CropImage.activity()
            .setMinCropResultSize(DATAv.MIX_SQUARE, DATAv.MIX_SQUARE)
            .setAspectRatio(1, 1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun CropImageSlider(activity: Activity?) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setMinCropResultSize(DATAv.MIX_SLIDER_X, DATAv.MIX_SLIDER_Y)
            .setAspectRatio(16, 9)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity!!)
    }

    fun Intro(context: Context?, background: ImageView, backWhite: ImageView, backDark: ImageView) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context!!)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            background.setImageResource(R.drawable.background_day)
            backWhite.visibility = View.VISIBLE
            backDark.visibility = View.GONE
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            background.setImageResource(R.drawable.background_night)
            backWhite.visibility = View.GONE
            backDark.visibility = View.VISIBLE
        }
    }

    fun Logo(context: Context?, background: ImageView) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context!!)
        if (sharedPreferences.getString("color_option", "ONE") == "ONE") {
            background.setImageResource(R.drawable.logo)
        } else if (sharedPreferences.getString("color_option", "NIGHT_ONE") == "NIGHT_ONE") {
            background.setImageResource(R.drawable.logo_night)
        }
    }

    fun getFileExtension(uri: Uri?, context: Context): String? {
        val cR = context.contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri!!))
    }

    fun moreDeleteCategory(
        activity: Activity,
        item: Category?,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val id = DATAv.EMPTY + item!!.id
        val name = DATAv.EMPTY + item.name
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra(activity, CLASSv.CATEGORY_EDIT, DATAv.CATEGORY_ID, id)
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity,
                        id,
                        name,
                        DATAv.CATEGORY,
                        DATAv.CATEGORIES,
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

    fun moreDeleteAlbum(
        activity: Activity,
        item: Album?,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val id = DATAv.EMPTY + item!!.id
        val category = DATAv.EMPTY + item.categoryId
        val artist = DATAv.EMPTY + item.artistId
        val name = DATAv.EMPTY + item.name
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra3(
                        activity,
                        CLASSv.ALBUM_EDIT,
                        DATAv.ALBUM_ID,
                        id,
                        DATAv.CATEGORY_ID,
                        category,
                        DATAv.ARTIST_ID,
                        artist
                    )
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity,
                        id,
                        name,
                        DATAv.ALBUM,
                        DATAv.ALBUMS,
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

    fun moreDeleteArtist(
        activity: Activity,
        item: Artist?,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val id = DATAv.EMPTY + item!!.id
        val name = DATAv.EMPTY + item.name
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra(activity, CLASSv.ARTIST_EDIT, DATAv.ARTIST_ID, id)
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity,
                        id,
                        name,
                        DATAv.ARTIST,
                        DATAv.ARTISTS,
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

    fun moreDeleteSong(
        activity: Activity,
        item: Song?,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val id = DATAv.EMPTY + item!!.id
        val name = DATAv.EMPTY + item.name
        val category = DATAv.EMPTY + item.categoryId
        val artist = DATAv.EMPTY + item.artistId
        val album = DATAv.EMPTY + item.albumId
        val options = arrayOf("Edit", "Delete")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog: DialogInterface?, which: Int ->
                if (which == 0) {
                    IntentExtra4(
                        activity,
                        CLASSv.SONG_EDIT,
                        DATAv.SONG_ID,
                        id,
                        DATAv.CATEGORY_ID,
                        category,
                        DATAv.ARTIST_ID,
                        artist,
                        DATAv.ALBUM_ID,
                        album
                    )
                } else if (which == 1) {
                    dialogOptionDelete(
                        activity,
                        id,
                        name,
                        DATAv.SONG,
                        DATAv.SONGS,
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

    fun dialogOptionDelete(
        activity: Activity,
        id: String?,
        name: String,
        type: String?,
        nameDB: String?,
        isEditorsChoice: Boolean,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val title = dialog.findViewById<TextView>(R.id.title)
        title.text = MessageFormat.format("Do you want to delete {0} ( {1} ) ?", name, type)
        dialog.findViewById<View>(R.id.yes).setOnClickListener {
            if (isEditorsChoice) {
                dialogUpdateEditorsChoice(dialog, activity, id)
            } else {
                deleteDB(
                    dialog,
                    activity,
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
        dialog.findViewById<View>(R.id.no).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun dialogUpdateEditorsChoice(dialogDelete: Dialog, context: Context?, id: String?) {
        val dialog = ProgressDialog(context)
        dialog.setMessage("Updating Editors Choice...")
        dialog.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATAv.EDITORS_CHOICE] = 0
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
            dialogDelete.dismiss()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(context, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
            dialogDelete.dismiss()
        }
    }

    fun deleteDB(
        dialogDelete: Dialog,
        activity: Activity,
        id: String?,
        name: String,
        nameDB: String?,
        DB: String?,
        idDB: String?,
        childDB: String?,
        DB2: String?,
        idDB2: String?,
        childDB2: String?,
        DB3: String?,
        idDB3: String?,
        childDB3: String?,
    ) {
        val dialog = ProgressDialog(activity)
        dialog.setTitle("Please wait")
        dialog.setMessage("Deleting $name ...")
        dialog.show()
        val reference = FirebaseDatabase.getInstance().getReference(nameDB!!)
        reference.child(id!!).removeValue().addOnSuccessListener {
            incrementItemRemoveCount(DB, idDB, childDB)
            incrementItemRemoveCount(DB2, idDB2, childDB2)
            incrementItemRemoveCount(DB3, idDB3, childDB3)
            DATAv.isChange = true
            activity.onBackPressed()
            dialog.dismiss()
            Toast.makeText(activity, "$name Deleted Successfully...", Toast.LENGTH_SHORT).show()
            dialogDelete.dismiss()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(activity, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun addToEditorsChoice(
        context: Context?, activity: Activity, id: String?,
        number: Int,
    ) {
        val dialog = ProgressDialog(context)
        dialog.setMessage("Updating Editors Choice...")
        dialog.show()
        val hashMap = HashMap<String?, Any>()
        hashMap[DATAv.EDITORS_CHOICE] = number
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.SONGS)
        reference.child(id!!).updateChildren(hashMap).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(context, "Editors Choice updated...", Toast.LENGTH_SHORT).show()
            activity.finish()
        }.addOnFailureListener { e: Exception ->
            dialog.dismiss()
            Toast.makeText(context, "Failed to update db duo to " + e.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun dataName(database: String?, dataId: String?, name: TextView) {
        val reference = FirebaseDatabase.getInstance().getReference(database!!)
        reference.child(dataId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val Name = DATAv.EMPTY + snapshot.child(DATAv.NAME).value
                name.text = MessageFormat.format("{0}{1}", DATAv.EMPTY, Name)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun dialogAboutArtist(context: Context?, imageDB: String?, nameDB: String?, aboutDB: String?) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_about_artist)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val image = dialog.findViewById<ImageView>(R.id.image)
        val name = dialog.findViewById<TextView>(R.id.name)
        val aboutTheArtist = dialog.findViewById<TextView>(R.id.aboutTheArtist)
        Glide(true, context, imageDB, image)
        name.text = MessageFormat.format("{0}{1}", DATAv.EMPTY, nameDB)
        aboutTheArtist.text = MessageFormat.format("{0}{1}", DATAv.EMPTY, aboutDB)
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun convertDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}