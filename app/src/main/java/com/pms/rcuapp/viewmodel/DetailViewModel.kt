package com.pms.rcuapp.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pms.rcuapp.uttils.Utils
import com.pms.rcuapp.model.base.BaseViewModel
import com.pms.rcuapp.model.getverificationDetailResponse.GetVerificationDetailData
import com.pms.rcuapp.model.getverificationDetailResponse.GetVerificationDetailResponse
import com.pms.rcuapp.network.CallbackObserver
import com.pms.rcuapp.network.Networking
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val context: Context) : BaseViewModel(){


  //  lateinit var getVerificationDetailData : GetVerificationDetailData

    var isData = MutableLiveData<Boolean>()

    var getVerificationDetailData: MutableLiveData<GetVerificationDetailData> = MutableLiveData()

    fun init(context: Context) {
       isData.value = false
     getVerificationRequestDetail()
    }

    private fun getVerificationRequestDetail() {
        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .getVerificationRequestDetail(AppConstants.verificationId.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetVerificationDetailResponse>() {
                    override fun onSuccess(response: GetVerificationDetailResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    override fun onNext(t: GetVerificationDetailResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                           if(t.getData() != null){
                               isData.value = true
                               getVerificationDetailData.postValue(t.getData())
                           }
                        }else{
                            Utils().showToast(context,t.getMessage().toString())
                        }
                    }
                })
        }else{

        }

    }

}