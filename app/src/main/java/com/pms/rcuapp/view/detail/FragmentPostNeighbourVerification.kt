package com.pms.rcuapp.view.detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.pms.rcuapp.databinding.FragmentPostNeighbourVerificationBinding
import com.pms.rcuapp.interfaces.FragmentLifecycleInterface
import com.pms.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Utility.Companion.setAllEnabled
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.verificationDetail.PostNeighbourVerificationViewModel


class FragmentPostNeighbourVerification  : BaseFragment(), FragmentLifecycleInterface {


    private var _binding: FragmentPostNeighbourVerificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val postNeighbourVerificationViewModel by lazy { PostNeighbourVerificationViewModel(context as Activity,binding) }

    var data : String = ""



    companion object {
        fun newInstance(selectedData: GetVerificationDetailData?): FragmentPostNeighbourVerification {
            val bundle = Bundle()
            val fragmentPostNeighbourVerification = FragmentPostNeighbourVerification()
            fragmentPostNeighbourVerification.arguments = bundle
            return fragmentPostNeighbourVerification
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostNeighbourVerificationBinding.inflate(inflater, container, false)

        binding.viewModel = postNeighbourVerificationViewModel
        binding.lifecycleOwner = this
        context?.let { postNeighbourVerificationViewModel.init(it) }
//      basicInformationModel.init(context, FragmentDetail.selectedData!!)

        postNeighbourVerificationViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }
        setView()
        Log.e("OnCrete","PhotoNeighbour")
        return binding.root
    }

    private fun setView() {
        postNeighbourVerificationViewModel.isNeighbour1StatusReconised.observeForever {
            if (it){
                binding.inpNeighbour1Remark.visibility = View.GONE
            }
            else{
                binding.inpNeighbour1Remark.visibility = View.VISIBLE
            }
        }

        postNeighbourVerificationViewModel.isNeighbour2StatusReconised.observeForever {
            if (it){
                binding.inpNeighbour2Remark.visibility = View.GONE
            }
            else{
                binding.inpNeighbour2Remark.visibility = View.VISIBLE
            }
        }


        if(ActivityDetail.selectedData!!.getStatus() != null){
            if(ActivityDetail.selectedData!!.getStatus() == AppConstants.statusPending){
              //  binding.constraintLayout.forEach { child -> child.setAllEnabled(false) }
            }
            else{
                binding.constraintLayout.forEach { child -> child.setAllEnabled(true) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPauseFragment() {

    }

    override fun onResumeFragment(s: String?) {
        Log.e("OnResume","Post Neighbour")

    }

}