package com.pms.rcuapp.view.menu

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.DashboardFragmentBinding
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestData
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Session.Companion.DATA
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.view.detail.ActivityDetail
import com.pms.rcuapp.viewmodel.DashboardViewModel


class DashboardFragment: BaseFragment() {

    private var _binding: DashboardFragmentBinding? = null

    private val binding get() = _binding!!
    private val dashboardViewModel by lazy { DashboardViewModel(activity as Context,this@DashboardFragment,binding) }

    // Location
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    var rcuType: String = "";

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener!!) }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = dashboardViewModel
        binding.lifecycleOwner = this

        dashboardViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        dashboardViewModel.totalVerification.observeForever {
            binding.totalVerificationCount.text = dashboardViewModel.totalVerification.value.toString()
        }

        binding.refreshLayout.setOnRefreshListener {
            dashboardViewModel.getPendingRequest()
        }

        binding.layoutDetail.tvTitle.text = "Dashboard"

        binding.layoutDetail.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_dashboardMenuFragment)
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            findNavController().navigate(R.id.action_dashboardFragment_to_dashboardMenuFragment)
        }

        try {
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
            (context as DashboardActivity).setTitle()
        }catch (e : Exception){
        }

        return binding.root
    }

    fun  redirectToDetailScreen(getPendingRequestData: GetPendingRequestData) {
        val bundle = Bundle()
        bundle.putSerializable(DATA, getPendingRequestData.getFirequestId())
        AppConstants.verificationId = getPendingRequestData.getFirequestId()!!
        //findNavController().navigate(R.id.action_DashboardFragment_to_FragmentDetail)

        val iDetain = Intent(context, ActivityDetail::class.java)
        startActivity(iDetain)
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.init(requireContext())
        Log.e("OnResume","Dashboard")
    }
}