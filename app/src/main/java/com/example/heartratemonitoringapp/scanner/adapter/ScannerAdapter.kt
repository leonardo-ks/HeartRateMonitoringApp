package com.example.heartratemonitoringapp.scanner.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.heartratemonitoringapp.databinding.ItemDeviceListBinding

class ScannerAdapter : RecyclerView.Adapter<ScannerAdapter.ViewHolder>() {

    private val mLeDevices: ArrayList<BluetoothDevice> = ArrayList()
    var onItemClick: ((BluetoothDevice) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<BluetoothDevice>) {
        this.mLeDevices.clear()
        this.mLeDevices.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mLeDevices[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mLeDevices.size

    inner class ViewHolder(private val binding: ItemDeviceListBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("MissingPermission", "CheckResult")
        fun bind(item: BluetoothDevice) {
            binding.tvDeviceName.text = item.name ?: "Unknown Device"
            binding.tvDeviceAddress.text = item.address
        }
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(mLeDevices[adapterPosition])
            }
        }
    }
}