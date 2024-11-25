package com.pms.rcuapp.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.databinding.ItemDashboardBinding
import com.pms.rcuapp.databinding.ItemReportingmanagerBinding
import com.pms.rcuapp.model.getverificationDetailResponse.GetReportEmployee
import com.pms.rcuapp.model.getverificationDetailResponse.GetReportingManager
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestData
import com.pms.rcuapp.viewmodel.DashboardViewModel
import com.pms.rcuapp.viewmodel.DetailViewModel
import com.pms.rcuapp.viewmodel.verificationDetail.BasicInformationViewModel

class RelationManagerViewHolder (val binding: ItemReportingmanagerBinding, val viewModel: BasicInformationViewModel) :  RecyclerView.ViewHolder(binding.root){


    fun bind(data: GetReportingManager) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel
    }
}