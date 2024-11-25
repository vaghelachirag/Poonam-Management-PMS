package com.pms.rcuapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.ItemDashboardBinding
import com.pms.rcuapp.databinding.ItemReportingmanagerBinding
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestData
import com.pms.rcuapp.uttils.Utility
import com.pms.rcuapp.viewmodel.DashboardViewModel
import com.pms.rcuapp.interfaces.OnItemSelected
import com.pms.rcuapp.model.getverificationDetailResponse.GetReportEmployee
import com.pms.rcuapp.model.getverificationDetailResponse.GetReportingManager
import com.pms.rcuapp.viewmodel.verificationDetail.BasicInformationViewModel


class RelationEmployeeAdapter(val context: Context, private val list: ArrayList<GetReportingManager>, val viewModel: BasicInformationViewModel, val onItemSelected: OnItemSelected<GetReportEmployee>,) :  RecyclerView.Adapter<RelationManagerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelationManagerViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemReportingmanagerBinding>(
            layoutInflater,
            R.layout.item_reportingmanager,
            parent,
            false
        )
        return RelationManagerViewHolder(binder, viewModel)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RelationManagerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(list[position])

        holder.binding.txtApplicateName.text = list[position].getEmployee()!!.getName()
        holder.binding.txtApplicateMobileNumber.text = list[position].getEmployee()!!.getMobileNo()

    }
    override fun getItemCount(): Int {
        return list.size
    }
}