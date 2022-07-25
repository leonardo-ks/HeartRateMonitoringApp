package com.example.heartratemonitoringapp.dashboard.profile.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.domain.usecase.model.UserDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ItemContactListBinding

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private val itemList: ArrayList<UserDataDomain> = ArrayList()
    private var deleteButtonState = false
    var onItemClick: ((UserDataDomain) -> Unit)? = null
    var onDeleteClick: ((Int) -> Unit)? = null

    fun setData(list: List<UserDataDomain>) {
        this.itemList.clear()
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun setButtonState(state: Boolean) {
        deleteButtonState = state
        notifyItemRangeChanged(0, itemList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: ItemContactListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDataDomain) {
            binding.tvName.text = item.name
            binding.tvEmail.text = item.email
            Glide.with(binding.root)
                .load(item.profile)
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .into(binding.ivProfile)
            if (deleteButtonState) {
                binding.btnDeleteContact.visibility = View.VISIBLE
                binding.btnDeleteContact.isEnabled = deleteButtonState
            } else {
                binding.btnDeleteContact.visibility = View.INVISIBLE
                binding.btnDeleteContact.isEnabled = deleteButtonState
            }
        }
        init {
            binding.btnDeleteContact.setOnClickListener {
                itemList[adapterPosition].id?.let { it1 -> onDeleteClick?.invoke(it1) }
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(itemList[adapterPosition])
            }
        }
    }
}