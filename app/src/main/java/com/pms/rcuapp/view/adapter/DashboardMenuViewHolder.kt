package com.pms.rcuapp.view.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.ItemDashboardBinding
import com.pms.rcuapp.databinding.ItemDashboardSelectionBinding
import com.pms.rcuapp.model.dashboard.getDashboardApiResponse.GetMobileDashboardDetailDto
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestData
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.viewmodel.DashboardMenuViewModel
import com.pms.rcuapp.viewmodel.DashboardViewModel

class DashboardMenuViewHolder (val context: Context, val binding: ItemDashboardSelectionBinding, val viewModel: DashboardMenuViewModel) :  RecyclerView.ViewHolder(binding.root) {


    fun bind(data: GetMobileDashboardDetailDto) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel


        Glide.with(context)
            .load(data.getIcon())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.image);
    }
}