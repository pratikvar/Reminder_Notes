package com.skybase.remindernotes.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skybase.remindernotes.R
import com.skybase.remindernotes.databinding.ListitemNoteBinding
import com.skybase.remindernotes.viewmodel.NoteModel


class NoteActivityAdapter(var interactionListener: OnNoteInteractionListener) :
    RecyclerView.Adapter<NoteActivityAdapter.NoteViewHolder>() {

    var mModelList: List<NoteModel>? = null

    private var mSelectedViewHolder: NoteViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        var binding: ListitemNoteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.listitem_note, parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val model = mModelList?.get(position)
        holder.binding.note = model

        setupListeners(holder, model)
    }

    private fun setupListeners(holder: NoteViewHolder, model: NoteModel?) {
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
            return@setOnLongClickListener true;
        }

        holder.binding.layoutOverlapIcons.setOnClickListener(null)
        holder.binding.layoutOverlapIcons.setOnLongClickListener {
            toggleOptionLayout(holder)
            return@setOnLongClickListener true;
        }

//        TransitionManager.beginDelayedTransition(
//            holder.binding.layoutOverlapIcons,
//            Slide(Gravity.BOTTOM)
//        )
    }

    private fun toggleOptionLayout(holder: NoteViewHolder) {
        if (holder.binding.layoutOverlapIcons.visibility == View.VISIBLE) {
            holder.binding.layoutOverlapIcons.visibility = View.GONE
        } else {
            if (mSelectedViewHolder != null) {
                if (mSelectedViewHolder?.binding?.layoutOverlapIcons?.visibility == View.VISIBLE) {
                    mSelectedViewHolder?.binding?.layoutOverlapIcons?.visibility = View.GONE
                }
            }
            holder.binding.layoutOverlapIcons.visibility = View.VISIBLE
            mSelectedViewHolder = holder
        }
    }

    override fun getItemCount(): Int {
        return mModelList?.size ?: 0
    }

    public fun updateDataSet(updatedList: List<NoteModel>?) {
        mModelList = updatedList
        notifyDataSetChanged()
    }

    public fun dismissActiveOptionOverlay() {
        if (mSelectedViewHolder != null) {
            toggleOptionLayout(mSelectedViewHolder!!)
        }
    }

    class NoteViewHolder(var binding: ListitemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    interface OnNoteInteractionListener {
        fun onNoteClicked(noteModel: NoteModel?)
        fun onNotePinClicked(noteModel: NoteModel?)
        fun onNoteShareClicked(noteModel: NoteModel?)
        fun onNoteDeleteClicked(noteModel: NoteModel?)
    }
}