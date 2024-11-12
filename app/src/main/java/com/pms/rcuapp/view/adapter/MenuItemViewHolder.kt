package com.pms.rcuapp.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.databinding.LayoutMenuItemBinding
import com.pms.rcuapp.model.getMenuListResponse.GetMenuListData


class MenuItemViewHolder(val binding: LayoutMenuItemBinding) :  RecyclerView.ViewHolder(binding.root) {
    fun bind(data: GetMenuListData) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
    }

}