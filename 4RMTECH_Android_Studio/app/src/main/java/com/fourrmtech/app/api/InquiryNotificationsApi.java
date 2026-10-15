package com.fourrmtech.app.api;
import com.fourrmtech.app.models.InquiryNotificationsResponse;
import com.fourrmtech.app.models.MarkInquiriesReadRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
public interface InquiryNotificationsApi {
 @GET("get_inquiry_notifications.php") Call<InquiryNotificationsResponse> getNotifications();
 @POST("mark_inquiries_read.php") Call<InquiryNotificationsResponse> markRead(@Body MarkInquiriesReadRequest request);
}
