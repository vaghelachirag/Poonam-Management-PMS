package com.pms.rcuapp.viewmodel.verificationDetail

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pms.rcuapp.uttils.Utils
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.FragmentRcoVerificationBinding
import com.pms.rcuapp.model.base.BaseViewModel
import com.pms.rcuapp.model.getMenuWebUrlResponse.GetMenuURLResponse
import com.pms.rcuapp.network.CallbackObserver
import com.pms.rcuapp.network.Networking
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Utility
import com.pms.rcuapp.view.menu.DashboardActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RCOVerificationViewModel(private val context: Context, private  val binding: FragmentRcoVerificationBinding) : BaseViewModel() {

    var webViewURL = MutableLiveData<String>()


    fun init(context: Context?) {
        getRcuVerificationURL("1001")
    }



    private fun getRcuVerificationURL(menuId: String) {

        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .getRcuVerificationWebpage(menuId,
                    DashboardActivity.currentLat.toString(),
                    DashboardActivity.currentLong.toString(),
                    AppConstants.verificationId.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetMenuURLResponse>() {
                    override fun onSuccess(response: GetMenuURLResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    override fun onNext(t: GetMenuURLResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                            if(t.getData() != null){
                                webViewURL.value = t.getData()!!.getUrl()
                                Log.e("URL",webViewURL.value.toString())
                            }
                        }else{
                            Utils().showToast(context,t.getMessage().toString())
                        }
                        Log.e("StatusCode",t.getStatus().toString())
                    }

                })
        }else{
            Utils().showToast(context,context.getString(R.string.nointernetconnection).toString())
        }
    }
}