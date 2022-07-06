package com.example.heartratemonitoringapp.form.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.heartratemonitoringapp.databinding.ItemLabelsBinding

class FormAdapter : RecyclerView.Adapter<FormAdapter.ViewHolder>() {

    private val itemList: ArrayList<String> = ArrayList()
    private var checkBoxState = false

    fun setData(list: List<String>) {
        this.itemList.clear()
        this.itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun setCheckbox(state: Boolean) {
        checkBoxState = state
        notifyItemRangeChanged(0, itemList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemLabelsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(private val binding: ItemLabelsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvLabelName.text = item
            if (checkBoxState) {
                binding.checkBox.visibility = View.VISIBLE
                binding.checkBox.isEnabled = checkBoxState
            } else {
                binding.checkBox.visibility = View.INVISIBLE
                binding.checkBox.isEnabled = checkBoxState
            }
        }
    }
}