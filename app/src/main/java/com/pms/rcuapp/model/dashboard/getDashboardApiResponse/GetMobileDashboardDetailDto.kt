package com.pms.rcuapp.model.dashboard.getDashboardApiResponse

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class GetMobileDashboardDetailDto {
    @SerializedName("orderNo")
    @Expose
    private var orderNo: Int? = null

    @SerializedName("buttonId")
    @Expose
    private var buttonId: Int? = null

    @SerializedName("buttonCaption")
    @Expose
    private var buttonCaption: String? = null

    @SerializedName("additionalCaption")
    @Expose
    private var additionalCaption: String? = null

    @SerializedName("isWebView")
    @Expose
    private var isWebView: Boolean? = null

    @SerializedName("webUrl")
    @Expose
    private var webUrl: String? = null

    @SerializedName("icon")
    @Expose
    private var icon: String? = null

    fun getOrderNo(): Int? {
        return orderNo
    }

    fun setOrderNo(orderNo: Int?) {
        this.orderNo = orderNo
    }

    fun getButtonId(): Int? {
        return buttonId
    }

    fun setButtonId(buttonId: Int?) {
        this.buttonId = buttonId
    }

    fun getButtonCaption(): String? {
        return buttonCaption
    }

    fun setButtonCaption(buttonCaption: String?) {
        this.buttonCaption = buttonCaption
    }

    fun getAdditionalCaption(): String? {
        return additionalCaption
    }

    fun setAdditionalCaption(additionalCaption: String?) {
        this.additionalCaption = additionalCaption
    }

    fun getIsWebView(): Boolean? {
        return isWebView
    }

    fun setIsWebView(isWebView: Boolean?) {
        this.isWebView = isWebView
    }

    fun getWebUrl(): String? {
        return webUrl
    }

    fun setWebUrl(webUrl: String?) {
        this.webUrl = webUrl
    }

    fun getIcon(): String? {
        return icon
    }

    fun setIcon(icon: String?) {
        this.icon = icon
    }

}