package com.pms.rcuapp.model.getverificationDetailResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GetAllocator {

    @SerializedName("recordId")
    @Expose
    private var recordId: Int? = null

    @SerializedName("financialInstituteId")
    @Expose
    private var financialInstituteId: Int? = null

    @SerializedName("employeeId")
    @Expose
    private var employeeId: Int? = null

    @SerializedName("employee")
    @Expose
    private var employee: GetAllocatorEmployee? = null

    fun getRecordId(): Int? {
        return recordId
    }

    fun setRecordId(recordId: Int?) {
        this.recordId = recordId
    }

    fun getFinancialInstituteId(): Int? {
        return financialInstituteId
    }

    fun setFinancialInstituteId(financialInstituteId: Int?) {
        this.financialInstituteId = financialInstituteId
    }

    fun getEmployeeId(): Int? {
        return employeeId
    }

    fun setEmployeeId(employeeId: Int?) {
        this.employeeId = employeeId
    }

    fun getEmployee(): GetAllocatorEmployee? {
        return employee
    }

    fun setEmployee(employee: GetAllocatorEmployee?) {
        this.employee = employee
    }
}