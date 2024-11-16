package com.pms.rcuapp.viewmodel.verificationDetail

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.pms.rcuapp.uttils.Utils
import com.pms.rcuapp.view.detail.ActivityDetail
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.FragmentPostNeighbourVerificationBinding
import com.pms.rcuapp.model.base.BaseViewModel
import com.pms.rcuapp.model.getPreNeighbourData.GetPreNeighbourResponse
import com.pms.rcuapp.model.getverificationDetailResponse.GetFirequestPostNeighboutVerificationDto
import com.pms.rcuapp.network.CallbackObserver
import com.pms.rcuapp.network.Networking
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PostNeighbourVerificationViewModel(private val context: Context, private val  binding: FragmentPostNeighbourVerificationBinding) : BaseViewModel() {



    // Login Params
    var neighbour3Name: ObservableField<String> = ObservableField()
    var neighbour4Name: ObservableField<String> = ObservableField()
    var neighbour3Remark: ObservableField<String> = ObservableField()
    var neighbour4Remark: ObservableField<String> = ObservableField()

    private var postNeighbourMutableLiveData: MutableLiveData<GetFirequestPostNeighboutVerificationDto> = MutableLiveData()

    var isNeighbour1StatusReconised = MutableLiveData<Boolean>()
    var isNeighbourStatus1ReconisedText = MutableLiveData<String>()
    var selectedNeighbour1StatusPosition = MutableLiveData<Int>()
    var selectedItemPosition: Int = 0

    var isNeighbour2StatusReconised = MutableLiveData<Boolean>()
    var isNeighbourStatus2ReconisedText = MutableLiveData<String>()
    var selectedNeighbour2StatusPosition = MutableLiveData<Int>()

    private var neighbourRecognisedList: List<String>? = null

    fun init(context: Context?) {

        val neighbourStatusList =  context!!.resources.getStringArray(R.array.neighbourstatus_array)
        neighbourRecognisedList = neighbourStatusList.asList()
        binding.spnNeighbour1Status.setListAdapter(neighbourRecognisedList)
        binding.spnNeighbour2Status.setListAdapter(neighbourRecognisedList)

        isNeighbour1StatusReconised.value = false
        isNeighbourStatus1ReconisedText.value = ""
        selectedNeighbour1StatusPosition.value = 0

        isNeighbour2StatusReconised.value = false
        isNeighbourStatus2ReconisedText.value = ""
        selectedNeighbour2StatusPosition.value = 0


        isNeighbourStatus1ReconisedText.observeForever {
            if (!it.equals("Positive")){
                binding.inpNeighbour1Remark.visibility = View.VISIBLE
            }else{
                binding.inpNeighbour1Remark.visibility = View.GONE
            }
        }

        isNeighbourStatus2ReconisedText.observeForever {
            if (!it.equals("Positive")){
                binding.inpNeighbour2Remark.visibility = View.VISIBLE
            }else{
                binding.inpNeighbour2Remark.visibility = View.GONE
            }
        }

        if (ActivityDetail.selectedData != null) {
            neighbour3Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour3Name().toString()))
            neighbour3Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour3Remark().toString()))
            neighbour4Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour4Name().toString()))
            neighbour4Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour4Remark().toString()))
            isNeighbour1StatusReconised.value = ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour3Remark().toString() != "false"
        }

        binding.spnNeighbour1Status.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()){
                    isNeighbour1StatusReconised.value = !(s.toString() == "Negative" || s.toString() == "Not Confirmed")
                    isNeighbourStatus1ReconisedText.value = s.toString()
                }else{
                    isNeighbour1StatusReconised.value = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.spnNeighbour1Status.setText(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour3Status().toString()))

        binding.spnNeighbour2Status.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()){
                    isNeighbour2StatusReconised.value = !(s.toString() == "Negative" || s.toString() == "Not Confirmed")
                    isNeighbourStatus2ReconisedText.value = s.toString()
                }else{
                    isNeighbour2StatusReconised.value = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        binding.spnNeighbour2Status.setText(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPostNeighboutVerificationDto()!!.getNeighbour4Status().toString()))
    }

    private fun setSelectedNeighbourRecognized(isText: String) {
        val pos =
            context.resources.getStringArray(R.array.neighbourstatus_array).indexOf(isText)
        if (pos >= 0) {
            selectedItemPosition = pos
        }
    }


    // On Saved Clicked
    fun onSaveClicked() {
       /*if (neighbour3Mobile.get().toString() == ""){
            Utils().showSnackBar(context,"Please Enter Neighbour1 Mobile Number",binding.constraintLayout)
        } else if (neighbour3Mobile.get().toString().length < 10){
            Utils().showSnackBar(context,"Please Enter Valid Neighbour1 Mobile Number",binding.constraintLayout)
        }
        else if (neighbour4Mobile.get().toString() == ""){
            Utils().showSnackBar(context,"Please Enter Neighbour2 Mobile Number",binding.constraintLayout)
        } else if (neighbour4Mobile.get().toString().length < 10){
            Utils().showSnackBar(context,"Please Enter Valid Neighbour2 Mobile Number",binding.constraintLayout)
        }else*/
        if (binding.spnNeighbour1Status.text.isNullOrEmpty() || binding.spnNeighbour1Status.text.equals("null")) {
           Utils().showSnackBar(context, "Please Select Status", binding.constraintLayout)
       }
        else {
            val model = GetFirequestPostNeighboutVerificationDto()
            model.setNeighbour3Name(neighbour3Name.get().toString())
            model.setNeighbour4Name(neighbour3Name.get().toString())
            model.setNeighbour3Remark(neighbour3Remark.get().toString())
            model.setNeighbour4Remark(neighbour4Remark.get().toString())
            model.setNeighbour3Status(isNeighbourStatus1ReconisedText.value.toString())
            model.setNeighbour4Status(isNeighbourStatus2ReconisedText.value.toString())
            postNeighbourMutableLiveData.value = model
            savePostNeighbourData(model)
        }
    }

    private fun savePostNeighbourData(model: GetFirequestPostNeighboutVerificationDto) {



        val params = HashMap<String,Any>()
        params["firequestId"] = AppConstants.verificationId.toString()
        params["neighbour3Name"] = model.getNeighbour3Name().toString()
        params["neighbour4Name"] = model.getNeighbour4Name().toString()
        params["Neighbour3Status"] = model.getNeighbour3Status().toString()
        params["Neighbour4Status"] = model.getNeighbour4Status().toString()
        params["neighbour3Remark"] = model.getNeighbour3Remark().toString()
        params["neighbour4Remark"] = model.getNeighbour4Remark().toString()



        if (Utility.isNetworkConnected(context)){
            isLoading.postValue(true)
            Networking.with(context)
                .getServices()
                .getSavePostNeighbourData(Networking.wrapParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetPreNeighbourResponse>() {
                    override fun onSuccess(response: GetPreNeighbourResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    override fun onNext(t: GetPreNeighbourResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                            Utils().showSnackBar(context,t.getMessage().toString(),binding.constraintLayout)
                        }else{
                            Utils().showSnackBar(context,t.getMessage().toString(),binding.constraintLayout)
                        }
                        Log.e("StatusCode",t.getStatus().toString())
                    }
                })
        }else{
            Utils().showSnackBar(context,context.getString(R.string.nointernetconnection).toString(),binding.constraintLayout)
        }
    }
}