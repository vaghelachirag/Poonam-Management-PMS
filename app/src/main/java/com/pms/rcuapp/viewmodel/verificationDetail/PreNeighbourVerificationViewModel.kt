package com.pms.rcuapp.viewmodel.verificationDetail

import android.annotation.SuppressLint
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
import com.pms.rcuapp.databinding.FragmentPreNeighbourVerificationBinding
import com.pms.rcuapp.model.base.BaseViewModel
import com.pms.rcuapp.model.getPreNeighbourData.GetPreNeighbourResponse
import com.pms.rcuapp.model.getverificationDetailResponse.GetFiRequestPreNeighboutVerificationDto
import com.pms.rcuapp.network.CallbackObserver
import com.pms.rcuapp.network.Networking
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class PreNeighbourVerificationViewModel(private val context: Context, val binding: FragmentPreNeighbourVerificationBinding) : BaseViewModel() {

    // Login Params
    var neighbour1Name: ObservableField<String> = ObservableField()
    var neighbour2Name: ObservableField<String> = ObservableField()
    var neighbour1Remark: ObservableField<String> = ObservableField()
    var neighbour2Remark: ObservableField<String> = ObservableField()


    private var preNeighbourMutableLiveData: MutableLiveData<GetFiRequestPreNeighboutVerificationDto> =
        MutableLiveData()

    var isNeighbour1StatusReconised = MutableLiveData<Boolean>()
    var isNeighbourStatus1ReconisedText = MutableLiveData<String>()
    var selectedNeighbour1StatusPosition = MutableLiveData<Int>()
    var selectedItemPosition: Int = 0

    var isNeighbour2StatusReconised = MutableLiveData<Boolean>()
    var isNeighbourStatus2ReconisedText = MutableLiveData<String>()
    var selectedNeighbour2StatusPosition = MutableLiveData<Int>()


    private var neighbourRecognisedList: List<String>? = null

    @SuppressLint("SuspiciousIndentation")
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

        setSelectedNeighbourStatus1(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Status().toString()))


        if (ActivityDetail.selectedData != null) {
            neighbour1Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Name().toString()))
            neighbour1Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Remark().toString()))
            neighbour2Name.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Name().toString()))
            neighbour2Remark.set(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Remark().toString()))
            isNeighbour1StatusReconised.value = ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Status().toString() != "false"
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

        Log.e("Status1",ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Status().toString())
        binding.spnNeighbour1Status.setText(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour1Status().toString()))

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
        binding.spnNeighbour2Status.setText(Utility.getNullToBlankString(ActivityDetail.selectedData!!.getFirequestPreNeighboutVerificationDto()!!.getNeighbour2Status().toString()))
    }

    private fun setSelectedNeighbourStatus1(isText: String) {
        val pos =
            context.resources.getStringArray(R.array.neighbourstatus_array).indexOf(isText)
        if (pos >= 0) {
            selectedItemPosition = pos
        }
    }


    // On Saved Clicked
    fun onSaveClicked() {
       /* if (neighbour1Mobile.get().toString() == "") {
            Utils().showSnackBar(
                context,
                "Please Enter Neighbour1 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour1Mobile.get().toString().length < 10) {
            Utils().showSnackBar(
                context,
                "Please Enter Valid Neighbour1 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour2Mobile.get().toString() == "") {
            Utils().showSnackBar(
                context,
                "Please Enter Neighbour2 Mobile Number",
                binding.constraintLayout
            )
        } else if (neighbour2Mobile.get().toString().length < 10) {
            Utils().showSnackBar(
                context,
                "Please Enter Valid Neighbour2 Mobile Number",
                binding.constraintLayout
            )
        } else*/
        if (binding.spnNeighbour1Status.text.isNullOrEmpty()) {
            Utils().showSnackBar(context, "Please Select Reason", binding.constraintLayout)
        }
        else {
            val model = GetFiRequestPreNeighboutVerificationDto()
            model.setNeighbour1Name(neighbour1Name.get().toString())
            model.setNeighbour2Name(neighbour2Name.get().toString())
            model.setNeighbour1Status(isNeighbourStatus1ReconisedText.value.toString())
            model.setNeighbour2Status(isNeighbourStatus1ReconisedText.value.toString())
            model.setNeighbour1Remark(neighbour1Remark.get().toString())
            model.setNeighbour2Remark(neighbour2Remark.get().toString())
            preNeighbourMutableLiveData.value = model
            savePreNeighbourData(model)
        }
    }

        private fun savePreNeighbourData(model: GetFiRequestPreNeighboutVerificationDto) {

            val params = HashMap<String, Any>()
            params["firequestId"] = AppConstants.verificationId.toString()
            params["neighbour1Name"] = model.getNeighbour1Name().toString()
            params["neighbour2Name"] = model.getNeighbour2Name().toString()
            params["Neighbour1Status"] = model.getNeighbour1Status().toString()
            params["Neighbour2Status"] = model.getNeighbour2Status().toString()
            params["neighbour1Remark"] = model.getNeighbour1Remark().toString()
            params["neighbour2Remark"] = model.getNeighbour2Remark().toString()


            if (Utility.isNetworkConnected(context)) {
                isLoading.postValue(true)
                Networking.with(context)
                    .getServices()
                    .getSavePreNeighbourData(Networking.wrapParams(params))
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
                            Log.e("Status", t.getStatusCode().toString())
                            isLoading.postValue(false)
                            if (t.getStatusCode() == 200) {
                                Utils().showSnackBar(
                                    context,
                                    t.getMessage().toString(),
                                    binding.constraintLayout
                                )
                            } else {
                                Utils().showSnackBar(
                                    context,
                                    t.getMessage().toString(),
                                    binding.constraintLayout
                                )
                            }
                            Log.e("StatusCode", t.getStatus().toString())
                        }
                    })
            } else {
                Utils().showSnackBar(
                    context,
                    context.getString(R.string.nointernetconnection).toString(),
                    binding.constraintLayout
                )
            }
        }
    }