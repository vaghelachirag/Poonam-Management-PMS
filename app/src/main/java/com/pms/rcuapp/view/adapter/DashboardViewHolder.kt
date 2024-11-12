package com.pms.rcuapp.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.databinding.ItemDashboardBinding
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestData
import com.pms.rcuapp.viewmodel.DashboardViewModel

class DashboardViewHolder (val binding: ItemDashboardBinding, val viewModel: DashboardViewModel) :  RecyclerView.ViewHolder(binding.root){


    fun bind(data: GetPendingRequestData) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel
    }
}