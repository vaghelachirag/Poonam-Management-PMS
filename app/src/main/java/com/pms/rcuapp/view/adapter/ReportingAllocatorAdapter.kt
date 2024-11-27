package com.pms.rcuapp.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.ItemReportingallocatorBinding
import com.pms.rcuapp.interfaces.OnItemSelected
import com.pms.rcuapp.model.getverificationDetailResponse.GetAllocator
import com.pms.rcuapp.model.getverificationDetailResponse.GetReportEmployee
import com.pms.rcuapp.viewmodel.verificationDetail.BasicInformationViewModel


class ReportingAllocatorAdapter(
    val context: Context,
    private val list: ArrayList<GetAllocator>,
    val viewModel: BasicInformationViewModel,
    val onItemSelected: OnItemSelected<GetReportEmployee>
) :  RecyclerView.Adapter<ReportingAllocatorViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportingAllocatorViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemReportingallocatorBinding>(
            layoutInflater,
            R.layout.item_reportingallocator,
            parent,
            false
        )
        return ReportingAllocatorViewHolder(binder, viewModel)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReportingAllocatorViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(list[position])

        holder.binding.txtApplicateName.text = list[position].getEmployee()!!.getName()
        holder.binding.txtApplicateMobileNumber.text = list[position].getEmployee()!!.getOfficialMobile()

        holder.binding.txtApplicateMobileNumber.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:"+ list[position].getEmployee()!!.getOfficialMobile()))
            context.startActivity(intent)
        }

    }
    override fun getItemCount(): Int {
        return list.size
    }
}