package com.pms.rcuapp.view.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pms.rcuapp.view.dialougs.FullScreenImageDialoug
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.ItemPicturesBinding
import com.pms.rcuapp.model.getverificationDetailResponse.GetFiVerificationDocument
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.viewmodel.verificationDetail.PictureViewModel


class PicturesAdapter(val context: Context, private val list: MutableList<GetFiVerificationDocument>, val viewModel: PictureViewModel) :
    RecyclerView.Adapter<PicturesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicturesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binder = DataBindingUtil.inflate<ItemPicturesBinding>(
            layoutInflater,
            R.layout.item_pictures,
            parent,
            false
        )

        return PicturesViewHolder(binder,parent.context, viewModel)
    }

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(list[position])
        holder.binding.ivPicture.setOnClickListener {
            Log.e("ImagePath",AppConstants.baseURLImage + "/"+ list[position].getDocumentPath().toString())
            FullScreenImageDialoug(context as Activity, AppConstants.baseURLImage + "/"+ list[position].getDocumentPath()).show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}