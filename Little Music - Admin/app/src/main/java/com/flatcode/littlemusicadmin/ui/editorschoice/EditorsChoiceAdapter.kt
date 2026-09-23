package com.flatcode.littlemusicadmin.ui.editorschoice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemusicadmin.databinding.ItemSongEditorsChoiceBinding
import com.flatcode.littlemusicadmin.model.EditorsChoice
import com.flatcode.littlemusicadmin.utils.DATA
import com.flatcode.littlemusicadmin.utils.dataName

class EditorsChoiceAdapter(
    private val onAddClick: (EditorsChoice) -> Unit,
    private val onChangeClick: (EditorsChoice) -> Unit,
    private val onRemoveClick: (EditorsChoice) -> Unit
) : ListAdapter<EditorsChoice, EditorsChoiceAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSongEditorsChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item, onAddClick, onChangeClick, onRemoveClick)
    }

    class ViewHolder(private val binding: ItemSongEditorsChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: EditorsChoice,
            onAddClick: (EditorsChoice) -> Unit,
            onChangeClick: (EditorsChoice) -> Unit,
            onRemoveClick: (EditorsChoice) -> Unit
        ) {
            binding.numberEditorsChoice.text = "${item.position}"
            val song = item.song

            binding.addCard.isVisible = song == null
            binding.detailsCard.isVisible = song != null
            binding.remove.isVisible = song != null
            binding.change.isVisible = song != null

            if (song != null) {
                binding.name.text = song.name
                binding.nrViews.text = song.viewsCount.toString()
                binding.nrLoves.text = song.lovesCount.toString()

                binding.artist.dataName(DATA.ARTISTS, song.artistId)
                binding.album.dataName(DATA.ALBUMS, song.albumId)
                binding.category.dataName(DATA.CATEGORIES, song.categoryId)

                binding.remove.setOnClickListener { onRemoveClick(item) }
                binding.change.setOnClickListener { onChangeClick(item) }
            } else {
                binding.add.setOnClickListener { onAddClick(item) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EditorsChoice>() {
            override fun areItemsTheSame(oldItem: EditorsChoice, newItem: EditorsChoice): Boolean =
                oldItem.position == newItem.position

            override fun areContentsTheSame(
                oldItem: EditorsChoice, newItem: EditorsChoice
            ): Boolean =
                oldItem.song?.id == newItem.song?.id && oldItem.song?.viewsCount == newItem.song?.viewsCount && oldItem.song?.lovesCount == newItem.song?.lovesCount
        }
    }
}
