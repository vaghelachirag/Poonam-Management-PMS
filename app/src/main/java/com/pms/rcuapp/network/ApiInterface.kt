package com.pms.rcuapp.network
import android.database.Observable
import com.pms.rcuapp.model.changepassword.GetChangePasswordResponse
import com.pms.rcuapp.model.dashboard.getDashboardApiResponse.GetDashboardApiResponse
import com.pms.rcuapp.model.finalSubmission.GetFinalSubmissionApiResponse
import com.pms.rcuapp.model.finalSubmission.SaveFinalSubmissionData
import com.pms.rcuapp.model.getAcceptRejectResponse.GetAcceptRejectResponse
import com.pms.rcuapp.model.getFiResidencePicture.GetFiResidencePictureResponse
import com.pms.rcuapp.model.getMenuListResponse.GetMenuListResponse
import com.pms.rcuapp.model.getMenuWebUrlResponse.GetMenuURLResponse
import com.pms.rcuapp.model.getPreNeighbourData.GetPreNeighbourResponse
import com.pms.rcuapp.model.getSaveResidenceVerificationResponse.GetSaveResidenceVerificationResponse
import com.pms.rcuapp.model.getUserProfileData.GetUserProfileResponse
import com.pms.rcuapp.model.getmasterData.GetMasterApiResponse
import com.pms.rcuapp.model.getverificationDetailResponse.GetVerificationDetailResponse
import com.pms.rcuapp.model.login.GetLoginResponseModel
import com.pms.rcuapp.model.pendingRequest.GetPendingRequestResponse
import com.pms.rcuapp.model.registerDevice.GetDeviceRegistrationResponse
import com.pms.rcuapp.model.saveresidenceverification.SaveVerificationDataDetail
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {

    @Headers("secret-key: MiPC9BjkCyGDFQXbSkoZcgqH3hpacKA76123J8322EpesabBDjsF23RTdsq8L123278956565452")
    @POST("api/login/mobileuserlogin")
    fun login(@Body requestBody: RequestBody): Observable<GetLoginResponseModel>

    @Headers("secret-key: MiPC9BjkCyGDFQXbSkoZcgqH3hpacKA76123J8322EpesabBDjsF23RTdsq8L123278956565452")
    @POST("api/MobileRegistrationRequest/Save")
    fun registerUser(@Body requestBody: RequestBody): Observable<GetDeviceRegistrationResponse>

    @GET("api/FIRequestVerificationView/GetPendingVerification")
    fun getPendingRequest(@Query("rcutype") Rcutype: String): Observable<GetPendingRequestResponse>

    @GET("api/MasterData/GetAllMaster")
    fun getMasterApiData(): Observable<GetMasterApiResponse>

    @GET("api/FiRequest/GetVerificationRecord")
    fun getVerificationRequestDetail(@Query("FIRequestId") requestId: String): Observable<GetVerificationDetailResponse>


    @POST("api/FiRequest/FIAcceptReject")
    fun getAcceptRejectResponse(@Body requestBody: RequestBody): Observable<GetAcceptRejectResponse>

    @POST("api/FiRequest/SavePreNeighbourDetail")
    fun getSavePreNeighbourData(@Body requestBody: RequestBody): Observable<GetPreNeighbourResponse>

    @POST("api/FiRequest/SavePostNeighbourDetail")
    fun getSavePostNeighbourData(@Body requestBody: RequestBody): Observable<GetPreNeighbourResponse>


    @GET("api/user/GetProfileRecord")
    fun getUserProfileData(): Observable<GetUserProfileResponse>


    @GET("api/MobileAppMenu/GetRecords")
    fun getMenuListResponse(): Observable<GetMenuListResponse>

    @POST("api/user/changePassword")
    fun getChangePasswordApiResponse(@Body requestBody: RequestBody): Observable<GetChangePasswordResponse>

    @GET("api/MobileAppMenu/GetWebViewUrl")
    fun getMenuURLResponse(@Query("menuId") menuId: String,@Query("Latitude") latitude: String,@Query("Longitude") longitude: String): Observable<GetMenuURLResponse>

    @POST("api/FiRequest/SaveVerification")
    fun getSaveFiResidenceResponse(@Body requestBody: SaveVerificationDataDetail): Observable<GetSaveResidenceVerificationResponse>


    @POST("api/FiRequest/SaveFirequestFinalSubmission")
    fun getSaveFinalSubmissionResponse(@Body requestBody: SaveFinalSubmissionData): Observable<GetFinalSubmissionApiResponse>


    @POST("api/FiRequest/SaveMobileAppFIDocument")
    fun saveSurveyPictureBase(@Body body: RequestBody): Observable<GetFinalSubmissionApiResponse?>?


    @POST("api/FiRequest/RemoveMobileAppFIDocument")
    fun deleteFiRequestPicture(@Body body: RequestBody): Observable<GetFinalSubmissionApiResponse?>?


    @GET("api/FiRequest/GetFIDocuments")
    fun getFiRequestPicture(@Query("FIRequestId") fiRequestId: String): Observable<GetFiResidencePictureResponse>


    @GET("api/MobileAppMenu/GetWebViewUrl")
    fun getRcuVerificationWebpage(@Query("MenuId") menuId: String,@Query("Latitude") latitude: String,@Query("Longitude") longitude: String,@Query("FIRequestId") fIRequestId: String): Observable<GetMenuURLResponse>

    @GET("api/MobileAppMenu/GetDashboard")
    fun getDashboardMenuResponse(): Observable<GetDashboardApiResponse>

}