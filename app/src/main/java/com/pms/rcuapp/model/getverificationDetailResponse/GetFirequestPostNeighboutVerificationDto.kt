package com.pms.rcuapp.model.getverificationDetailResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetFirequestPostNeighboutVerificationDto {


    @SerializedName("firequestId")
    @Expose
    private var firequestId: Int? = null

    @SerializedName("neighbour3Name")
    @Expose
    private var neighbour3Name: Any? = null

    @SerializedName("neighbour4Name")
    @Expose
    private var neighbour4Name: Any? = null

    @SerializedName("neighbour3Remark")
    @Expose
    private var neighbour3Remark: Any? = null

    @SerializedName("neighbour4Remark")
    @Expose
    private var neighbour4Remark: Any? = null

    @SerializedName("neighbour3Status")
    @Expose
    private var neighbour3Status: Any? = null

    @SerializedName("neighbour4Status")
    @Expose
    private var neighbour4Status: Any? = null

    fun getFirequestId(): Int? {
        return firequestId
    }

    fun setFirequestId(firequestId: Int?) {
        this.firequestId = firequestId
    }

    fun getNeighbour3Name(): Any? {
        return neighbour3Name
    }

    fun setNeighbour3Name(neighbour3Name: Any?) {
        this.neighbour3Name = neighbour3Name
    }

    fun getNeighbour4Name(): Any? {
        return neighbour4Name
    }

    fun setNeighbour4Name(neighbour4Name: Any?) {
        this.neighbour4Name = neighbour4Name
    }

    fun getNeighbour3Remark(): Any? {
        return neighbour3Remark
    }

    fun setNeighbour3Remark(neighbour3Remark: Any?) {
        this.neighbour3Remark = neighbour3Remark
    }

    fun getNeighbour4Remark(): Any? {
        return neighbour4Remark
    }

    fun setNeighbour4Remark(neighbour4Remark: Any?) {
        this.neighbour4Remark = neighbour4Remark
    }

    fun getNeighbour3Status(): Any? {
        return neighbour3Status
    }

    fun setNeighbour3Status(neighbour3Status: Any?) {
        this.neighbour3Status = neighbour3Status
    }

    fun getNeighbour4Status(): Any? {
        return neighbour4Status
    }

    fun setNeighbour4Status(neighbour4Status: Any?) {
        this.neighbour4Status = neighbour4Status
    }


}