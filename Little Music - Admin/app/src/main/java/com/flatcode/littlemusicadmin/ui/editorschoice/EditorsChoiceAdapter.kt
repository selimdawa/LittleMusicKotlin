package com.flatcode.littlemusicadmin.ui.editorschoice

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.model.EditorsChoice
import com.flatcode.littlemusicadmin.model.Song
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ItemSongEditorsChoiceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class EditorsChoiceAdapter(private val activity: Activity, var list: List<EditorsChoice>) :
    RecyclerView.Adapter<EditorsChoiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = position + 1
        val editorsChoiceId = DATA.EMPTY + id

        loadMusicDetails(
            id, editorsChoiceId, holder.binding.name, holder.binding.nrViews, holder.binding.nrLoves, holder.binding.artist,
            holder.binding.album, holder.binding.category, holder.binding.remove, holder.binding.change,
            holder.binding.addCard, holder.binding.detailsCard
        )

        holder.binding.numberEditorsChoice.text = MessageFormat.format("{0}{1}", DATA.EMPTY, id)
        holder.binding.add.setOnClickListener {
            activity.openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(DATA.EDITORS_CHOICE_ID to editorsChoiceId, DATA.OLD_ID to null)
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemSongEditorsChoiceBinding) : RecyclerView.ViewHolder(binding.root)

    private fun loadMusicDetails(
        i: Int, position: String, title: TextView, viewsCount: TextView, lovesCount: TextView,
        artist: TextView, album: TextView, category: TextView, remove: ImageView,
        change: ImageView, addCard: CardView, detailsCard: CardView,
    ) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(Song::class.java)!!
                    if (item.editorsChoice == i) {
                        val id = DATA.EMPTY + item.id
                        val name = DATA.EMPTY + item.name
                        loadData(id)
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                        remove.setOnClickListener {
                            activity.dialogOptionDelete(
                                id, name, DATA.EDITORS_CHOICE, DATA.EDITORS_CHOICE,
                                true, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL,
                                DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL
                            )
                        }
                        change.setOnClickListener {
                            activity.openActivity<EditorsChoiceAddActivity>(
                                extras = arrayOf(
                                    DATA.EDITORS_CHOICE_ID to position, DATA.OLD_ID to id
                                )
                            )
                        }
                    } else {
                        addCard.visibility = View.VISIBLE
                        detailsCard.visibility = View.GONE
                        remove.visibility = View.GONE
                        change.visibility = View.GONE
                    }
                }
            }

            private fun loadData(id: String) {
                val ref = FirebaseDatabase.getInstance().getReference(DATA.SONGS)
                ref.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val item = dataSnapshot.getValue(Song::class.java)!!
                        val name = DATA.EMPTY + item.name
                        val ViewsCount = DATA.EMPTY + item.viewsCount
                        val LovesCount = DATA.EMPTY + item.lovesCount
                        val artistId = DATA.EMPTY + item.artistId
                        val albumId = DATA.EMPTY + item.albumId
                        val categoryId = DATA.EMPTY + item.categoryId

                        title.text = name
                        viewsCount.text = ViewsCount
                        lovesCount.text = LovesCount
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                        artist.dataName(DATA.ARTISTS, artistId)
                        album.dataName(DATA.ALBUMS, albumId)
                        category.dataName(DATA.CATEGORIES, categoryId)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
