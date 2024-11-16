package com.pms.rcuapp.model.getverificationDetailResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GetFiRequestPreNeighboutVerificationDto {
    @SerializedName("firequestId")
    @Expose
    private var firequestId: Int? = null

    @SerializedName("neighbour1Name")
    @Expose
    private var neighbour1Name: String? = null

    @SerializedName("neighbour2Name")
    @Expose
    private var neighbour2Name: String? = null

    @SerializedName("neighbour1Remark")
    @Expose
    private var neighbour1Remark: String? = null

    @SerializedName("neighbour2Remark")
    @Expose
    private var neighbour2Remark: String? = null

    @SerializedName("neighbour1Status")
    @Expose
    private var neighbour1Status: String? = null

    @SerializedName("neighbour2Status")
    @Expose
    private var neighbour2Status: String? = null

    fun getFirequestId(): Int? {
        return firequestId
    }

    fun setFirequestId(firequestId: Int?) {
        this.firequestId = firequestId
    }

    fun getNeighbour1Name(): String? {
        return neighbour1Name
    }

    fun setNeighbour1Name(neighbour1Name: String?) {
        this.neighbour1Name = neighbour1Name
    }

    fun getNeighbour2Name(): String? {
        return neighbour2Name
    }

    fun setNeighbour2Name(neighbour2Name: String?) {
        this.neighbour2Name = neighbour2Name
    }

    fun getNeighbour1Remark(): String? {
        return neighbour1Remark
    }

    fun setNeighbour1Remark(neighbour1Remark: String?) {
        this.neighbour1Remark = neighbour1Remark
    }

    fun getNeighbour2Remark(): String? {
        return neighbour2Remark
    }

    fun setNeighbour2Remark(neighbour2Remark: String?) {
        this.neighbour2Remark = neighbour2Remark
    }

    fun getNeighbour1Status(): String? {
        return neighbour1Status
    }

    fun setNeighbour1Status(neighbour1Status: String?) {
        this.neighbour1Status = neighbour1Status
    }

    fun getNeighbour2Status(): String? {
        return neighbour2Status
    }

    fun setNeighbour2Status(neighbour2Status: String?) {
        this.neighbour2Status = neighbour2Status
    }
}