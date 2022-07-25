package com.example.heartratemonitoringapp.dashboard.profile.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.domain.usecase.model.UserDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.databinding.ItemAddContactListBinding

class AddContactAdapter : RecyclerView.Adapter<AddContactAdapter.ViewHolder>() {

    private val itemList: ArrayList<UserDataDomain> = ArrayList()
    private var deleteButtonState = false
    var onAddClick: ((Int) -> Unit)? = null

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
        ViewHolder(ItemAddContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: ItemAddContactListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDataDomain) {
            binding.tvName.text = item.name
            binding.tvEmail.text = item.email
            Glide.with(binding.root)
                .load(item.profile)
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .into(binding.ivProfile)
        }
        init {
            binding.btnAddContact.setOnClickListener {
                itemList[adapterPosition].id?.let { it1 -> onAddClick?.invoke(it1) }
            }
        }
    }
}