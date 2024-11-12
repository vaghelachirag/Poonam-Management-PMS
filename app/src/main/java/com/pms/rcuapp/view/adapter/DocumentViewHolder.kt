package com.pms.rcuapp.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.databinding.ItemDocumentBinding
import com.pms.rcuapp.model.getverificationDetailResponse.GetVerificationDocument
import com.pms.rcuapp.viewmodel.verificationDetail.BasicInformationViewModel

class DocumentViewHolder(val binding: ItemDocumentBinding, val viewModel: BasicInformationViewModel) :  RecyclerView.ViewHolder(binding.root) {

    fun bind(data: GetVerificationDocument) {
        binding.position = adapterPosition
        binding.holder = this
        binding.itemData = data
        binding.viewmodel = viewModel
    }
}