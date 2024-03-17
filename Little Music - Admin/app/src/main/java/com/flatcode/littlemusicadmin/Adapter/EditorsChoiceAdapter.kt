package com.flatcode.littlemusicadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.Model.EditorsChoice
import com.flatcode.littlemusicadmin.Model.Song
import com.flatcode.littlemusicadmin.Unit.CLASS
import com.flatcode.littlemusicadmin.Unit.DATA
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ItemSongEditorsChoiceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class EditorsChoiceAdapter(private val activity: Activity, var list: List<EditorsChoice>) :
    RecyclerView.Adapter<EditorsChoiceAdapter.ViewHolder>() {

    private var binding: ItemSongEditorsChoiceBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemSongEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = position + 1
        val editorsChoiceId = DATA.EMPTY + id

        loadMusicDetails(
            id, editorsChoiceId, holder.name, holder.nrViews, holder.nrLoves, holder.artist,
            holder.album, holder.category, holder.remove, holder.change,
            holder.addCard, holder.detailsCard
        )

        holder.numberEditorsChoice.text = MessageFormat.format("{0}{1}", DATA.EMPTY, id)
        holder.add.setOnClickListener {
            VOID.IntentExtra2(
                activity, CLASS.EDITORS_CHOICE_ADD, DATA.EDITORS_CHOICE_ID, editorsChoiceId,
                DATA.OLD_ID, null
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var add: ImageView
        var remove: ImageView
        var change: ImageView
        var name: TextView
        var album: TextView
        var artist: TextView
        var nrLoves: TextView
        var nrViews: TextView
        var category: TextView
        var numberEditorsChoice: TextView
        var item: LinearLayout
        var item2: LinearLayout
        var addCard: CardView
        var detailsCard: CardView

        init {
            nrLoves = binding!!.nrLoves
            nrViews = binding!!.nrViews
            name = binding!!.name
            album = binding!!.album
            artist = binding!!.artist
            category = binding!!.category
            item = binding!!.item
            item2 = binding!!.item2
            add = binding!!.add
            numberEditorsChoice = binding!!.numberEditorsChoice
            addCard = binding!!.addCard
            detailsCard = binding!!.detailsCard
            remove = binding!!.remove
            change = binding!!.change
        }
    }

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
                            VOID.dialogOptionDelete(
                                activity, id, name, DATA.EDITORS_CHOICE, DATA.EDITORS_CHOICE,
                                true, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL,
                                DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL, DATA.NULL
                            )
                        }
                        change.setOnClickListener {
                            VOID.IntentExtra2(
                                activity, CLASS.EDITORS_CHOICE_ADD,
                                DATA.EDITORS_CHOICE_ID, position, DATA.OLD_ID, id
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
                        //get data
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
                        VOID.dataName(DATA.ARTISTS, artistId, artist)
                        VOID.dataName(DATA.ALBUMS, albumId, album)
                        VOID.dataName(DATA.CATEGORIES, categoryId, category)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}