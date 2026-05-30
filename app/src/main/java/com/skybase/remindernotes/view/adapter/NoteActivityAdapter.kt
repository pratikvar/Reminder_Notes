package com.skybase.remindernotes.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skybase.remindernotes.databinding.ListitemNoteBinding
import com.skybase.remindernotes.view.NoteUiBinder
import com.skybase.remindernotes.viewmodel.NoteModel

class NoteActivityAdapter(private val interactionListener: OnNoteInteractionListener) :
    RecyclerView.Adapter<NoteActivityAdapter.NoteViewHolder>() {

    private var modelList: List<NoteModel>? = null
    private var selectedViewHolder: NoteViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ListitemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val model = modelList?.get(position) ?: return
        NoteUiBinder.bindListItem(holder.binding, model)
        setupListeners(holder, model)
    }

    private fun setupListeners(holder: NoteViewHolder, model: NoteModel) {
        holder.binding.root.setOnClickListener { interactionListener.onNoteClicked(model) }

        holder.binding.ivOptionPin.setOnClickListener {
            interactionListener.onNotePinClicked(model)
            holder.binding.layoutOverlapIcons.visibility = View.GONE
        }
        holder.binding.ivOptionDelete.setOnClickListener {
            interactionListener.onNoteDeleteClicked(model)
            holder.binding.layoutOverlapIcons.visibility = View.GONE
        }
        holder.binding.ivOptionShare.setOnClickListener {
            interactionListener.onNoteShareClicked(model)
            holder.binding.layoutOverlapIcons.visibility = View.GONE
        }

        holder.binding.root.setOnLongClickListener {
            toggleOptionLayout(holder)
            true
        }

        holder.binding.layoutOverlapIcons.setOnClickListener(null)
        holder.binding.layoutOverlapIcons.setOnLongClickListener {
            toggleOptionLayout(holder)
            true
        }
    }

    private fun toggleOptionLayout(holder: NoteViewHolder) {
        if (holder.binding.layoutOverlapIcons.visibility == View.VISIBLE) {
            holder.binding.layoutOverlapIcons.visibility = View.GONE
        } else {
            if (selectedViewHolder?.binding?.layoutOverlapIcons?.visibility == View.VISIBLE) {
                selectedViewHolder?.binding?.layoutOverlapIcons?.visibility = View.GONE
            }
            holder.binding.layoutOverlapIcons.visibility = View.VISIBLE
            selectedViewHolder = holder
        }
    }

    override fun getItemCount(): Int = modelList?.size ?: 0

    fun updateDataSet(updatedList: List<NoteModel>?) {
        modelList = updatedList
        notifyDataSetChanged()
    }

    fun dismissActiveOptionOverlay() {
        selectedViewHolder?.let { toggleOptionLayout(it) }
    }

    class NoteViewHolder(val binding: ListitemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnNoteInteractionListener {
        fun onNoteClicked(noteModel: NoteModel?)
        fun onNotePinClicked(noteModel: NoteModel?)
        fun onNoteShareClicked(noteModel: NoteModel?)
        fun onNoteDeleteClicked(noteModel: NoteModel?)
    }
}
