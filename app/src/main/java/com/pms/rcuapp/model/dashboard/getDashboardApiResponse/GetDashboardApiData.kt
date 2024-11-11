package com.pms.rcuapp.model.dashboard.getDashboardApiResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetDashboardApiData {

    @SerializedName("totalPendingCount")
    @Expose
    private var totalPendingCount: Int? = null

    @SerializedName("mobileDashboardDetailDto")
    @Expose
    private var mobileDashboardDetailDto: ArrayList<GetMobileDashboardDetailDto>? = null

    fun getTotalPendingCount(): Int? {
        return totalPendingCount
    }

    fun setTotalPendingCount(totalPendingCount: Int?) {
        this.totalPendingCount = totalPendingCount
    }

    fun getMobileDashboardDetailDto(): ArrayList<GetMobileDashboardDetailDto>? {
        return mobileDashboardDetailDto
    }

    fun setMobileDashboardDetailDto(mobileDashboardDetailDto: ArrayList<GetMobileDashboardDetailDto>?) {
        this.mobileDashboardDetailDto = mobileDashboardDetailDto
    }
}