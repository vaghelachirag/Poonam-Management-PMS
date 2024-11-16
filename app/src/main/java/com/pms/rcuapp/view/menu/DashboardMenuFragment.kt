package com.pms.rcuapp.view.menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.DashboardMenuFragmentBinding
import com.pms.rcuapp.model.dashboard.getDashboardApiResponse.GetMobileDashboardDetailDto
import com.pms.rcuapp.uttils.Utils
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.DashboardMenuViewModel

class DashboardMenuFragment: BaseFragment()  {

    private var _binding: DashboardMenuFragmentBinding? = null

    private val binding get() = _binding!!

    private val dashboardMenuViewModel by lazy { DashboardMenuViewModel(activity as Context,binding,this@DashboardMenuFragment) }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DashboardMenuFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = dashboardMenuViewModel
        binding.lifecycleOwner = this
        dashboardMenuViewModel.init()

        dashboardMenuViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        binding.refreshLayout.setOnRefreshListener {
            dashboardMenuViewModel.getDashboardMenu()
        }

        return binding.root
    }

    fun  redirectToDetailScreen(getDashboard: GetMobileDashboardDetailDto) {
        if (!getDashboard.getAdditionalCaption().isNullOrEmpty() && getDashboard.getAdditionalCaption().equals("0")){
            context?.let { Utils().showToast(it,"No Data Found!") }
        }else {
            val bundle = Bundle()
            bundle.putString("RcuType", getDashboard.getButtonCaption())
            findNavController().navigate(
                R.id.action_dashboardMenuFragment_to_dashboardFragment,
                bundle
            )
        }
    }

    fun  redirectToWebView(rcuType: String?) {
        val bundle = Bundle()
        bundle.putString("webURL", rcuType)
        findNavController().navigate(R.id.action_dashboardMenuFragment_to_webViewFragment,bundle)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_nav_menup)
        (context as DashboardActivity).setTitle()
    }
}