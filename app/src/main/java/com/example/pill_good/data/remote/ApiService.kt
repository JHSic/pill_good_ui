package com.example.pill_good.data.remote

import com.example.pill_good.data.dto.AutoMessageDTO
import com.example.pill_good.data.dto.EditOCRDTO
import com.example.pill_good.data.dto.GroupMemberAndUserIndexDTO
import com.example.pill_good.data.dto.LoginDTO
import com.example.pill_good.data.dto.MedicationInfoDTO
import com.example.pill_good.data.dto.NotificationDTO
import com.example.pill_good.data.dto.PillDTO
import com.example.pill_good.data.dto.PrescriptionAndDiseaseNameDTO
import com.example.pill_good.data.dto.SearchingConditionDTO
import com.example.pill_good.data.dto.TakePillAndTakePillCheckAndGroupMemberIndexDTO
import com.example.pill_good.data.dto.UserDTO
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

/**
 * Interface ApiService
 *
 * Retrofit2에서 REST API를 활용하기 위한 클라이언트 사이드 URI 매핑 정보를 모아놓은 인터페이스
 * Repository 계층에서 해당 인터페이스의 의존성을 주입받고 활용해 서버와 통신한다.
 *
 * 각 Repository는 서버와 통신이 필요한 메소드를 작성하게 된다.
 * 각 method의 return은 Call<T> 형식으로, 이는 Retrofit2에서 비동기 작업을 위한 객체이다.
 * 각 method는 suspend로 선언되어 코루틴 내부에서 비동기 작업을 수행하므로 리턴형식이 Call<T>인 것을 생략한다.
 * 또한, 각 method의 반환값은 ApiResponse 객체로 매핑하여 Data와 Response에 대한 정보를 분리한다.
 */
interface ApiService {


    /**
     * UserRepository
     */
    @DELETE("/user/delete/{id}")
    suspend fun deleteUserById(@Path("id") userId: Long): ApiResponse<Unit>

    @PUT("/user/update-token/{id}")
    suspend fun updateUserToken(@Path("id") userId: Long, @Body userDTO: UserDTO): ApiResponse<UserDTO>


    /**
     * LoginRepository
     */
    @POST("/login")
    suspend fun login(@Body loginDTO: LoginDTO): ApiResponse<UserDTO>


    /**
    * GroupMemberRepository
     */
    @GET("/group-member/search/group-members")
    suspend fun getGroupMembersByUserId(@Query("userIndex") userId: Long)
    : ApiResponse<List<GroupMemberAndUserIndexDTO>>

    @GET("/group-member/search/{id}")
    suspend fun getGroupMemberById(@Path("id") groupMemberId: Long)
    : ApiResponse<GroupMemberAndUserIndexDTO>

    @POST("/group-member/create")
    suspend fun createGroupMember(@Body groupMemberAndUserIndexDTO: GroupMemberAndUserIndexDTO)
    : ApiResponse<GroupMemberAndUserIndexDTO>

    @PUT("/group-member/update/{id}")
    suspend fun updateGroupMember(
        @Path("id") groupMemberId: Long,
        @Body groupMemberAndUserIndexDTO: GroupMemberAndUserIndexDTO)
    : ApiResponse<GroupMemberAndUserIndexDTO>

    @DELETE("/group-member/delete/{id}")
    suspend fun deleteGroupMember(@Path("id") groupMemberId: Long)
    : ApiResponse<Unit>


    /**
     * PillRepository
     */
    @GET("/pill/search/pill-index/{id}")
    suspend fun getPillById(@Path("id") id: Long)
    : ApiResponse<PillDTO>

    @GET("/pill/search/pill-name/{pill-name}")
    suspend fun getPillByPillName(@Path("pill-name") pillName: String)
    : ApiResponse<PillDTO>

    @POST("/pill/search/pills")
    suspend fun getPillBySearchingCondition(@Body searchingConditionDTO: SearchingConditionDTO)
    : ApiResponse<List<PillDTO>>


    /**
     * OCRRepository
     */
    @Multipart
    @POST("/ocr/create/original")
    suspend fun createInitialOCR(
        @Query("groupMemberIndex") groupMemberId: Long,
        @Query("groupMemberName") groupMemberName: String,
        @Query("dateStart") dateStart: LocalDate,
        @Query("userFcmToken") userFcmToken: String,
        @Part image: MultipartBody.Part): ApiResponse<Unit>

    @POST("/ocr/create")
    suspend fun createUpdatedOCR(@Body editOCR: EditOCRDTO): ApiResponse<Unit>


    /**
     * PrescriptionRepository
     */
    @GET("/prescription/search/{group-member-id}")
    suspend fun getPrescriptionByGroupMemberId(@Path("group-member-id") groupMemberId: Long)
    : ApiResponse<List<PrescriptionAndDiseaseNameDTO>>

    @DELETE("/prescription/delete/{id}")
    suspend fun deletePrescriptionById(@Path("id") id: Long)
    : ApiResponse<Unit>


    /**
     * TakePillRepository
     * LocalDate.parse()를 이용해 쿼리 파라미터로 넘겨주어야 함. (Retrofit2에서는 LocalDate 통신을 지원하지 않음)
     */
    @GET("/take-pill/search/calendar-data")
    suspend fun getCalendarDataByUserIdBetweenDate(
        @Query("userIndex") userId: Long,
        @Query("dateStart") dateStart: LocalDate,
        @Query("dateEnd") dateEnd: LocalDate
    ): ApiResponse<List<TakePillAndTakePillCheckAndGroupMemberIndexDTO>>

    @POST("/take-pill/search")
    suspend fun getTakePillsByGroupMemberIdListAndTargetDate(
        @Body groupMemberIndexList: List<Long>,
        @Query("targetDate") targetDate: LocalDate
    ): ApiResponse<List<MedicationInfoDTO>>


    /**
     * TakePillCheckRepository
     */
    @GET("/take-pill-check/update/take-check")
    suspend fun updateTakeCheck(@Query("takePillCheckIndex") id: Long, @Query("takeCheck") takeCheck: Boolean): ApiResponse<Unit>

    /**
     * NotificationRepository
     */
    @GET("/notification/search/{user-id}")
    suspend fun getNotificationByUserId(@Path("user-id") userId: Long): ApiResponse<List<NotificationDTO>>

    @PUT("/notification/update/notification-check/{id}")
    suspend fun updateNotificationCheckToTrue(@Path("id") id: Long): ApiResponse<NotificationDTO>


    /**
     * SendAutoMessageRepository
     */
    @GET("/send-auto-message/search/{user-id}")
    suspend fun getMessageContentByUserId(@Path("user-id") userId: Long): ApiResponse<List<AutoMessageDTO>>


}
