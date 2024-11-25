package com.pms.rcuapp.model.getverificationDetailResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GetReportingManager {

    @SerializedName("recordId")
    @Expose
    private var recordId: Int? = null

    @SerializedName("productId")
    @Expose
    private var productId: Int? = null

    @SerializedName("employeeId")
    @Expose
    private var employeeId: Int? = null

    @SerializedName("employee")
    @Expose
    private var employee: GetReportEmployee? = null

    fun getRecordId(): Int? {
        return recordId
    }

    fun setRecordId(recordId: Int?) {
        this.recordId = recordId
    }

    fun getProductId(): Int? {
        return productId
    }

    fun setProductId(productId: Int?) {
        this.productId = productId
    }

    fun getEmployeeId(): Int? {
        return employeeId
    }

    fun setEmployeeId(employeeId: Int?) {
        this.employeeId = employeeId
    }

    fun getEmployee(): GetReportEmployee? {
        return employee
    }

    fun setEmployee(employee: GetReportEmployee?) {
        this.employee = employee
    }
}