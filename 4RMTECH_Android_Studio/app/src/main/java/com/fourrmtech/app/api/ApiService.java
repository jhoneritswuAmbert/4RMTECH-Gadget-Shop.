package com.fourrmtech.app.api;

import com.fourrmtech.app.models.LoginRequest;
import com.fourrmtech.app.models.LoginResponse;
import com.fourrmtech.app.models.Product;
import com.fourrmtech.app.models.ProductResponse;
import com.fourrmtech.app.models.ProductSingleResponse;
import com.fourrmtech.app.models.RegisterRequest;
import com.fourrmtech.app.models.RegisterResponse;
import com.fourrmtech.app.models.CustomerResponse;
import com.fourrmtech.app.models.AppointmentResponse;
import com.fourrmtech.app.models.CreateAppointmentRequest;
import com.fourrmtech.app.models.CreateAppointmentResponse;
import com.fourrmtech.app.models.CancelAppointmentRequest;
import com.fourrmtech.app.models.UpdateCustomerRequest;
import com.fourrmtech.app.models.UpdateCategoryRequest;
import com.fourrmtech.app.models.DeleteCategoryRequest;
import com.fourrmtech.app.models.CategoryResponse;
import com.fourrmtech.app.models.AdminLoginRequest;
import com.fourrmtech.app.models.AdminLoginResponse;
import com.fourrmtech.app.models.AddProductRequest;
import com.fourrmtech.app.models.UpdateProductRequest;
import com.fourrmtech.app.models.DeleteProductRequest;
import com.fourrmtech.app.models.AddCategoryRequest;
import com.fourrmtech.app.models.UpdateAppointmentStatusRequest;
import com.fourrmtech.app.models.CustomerListResponse;
import com.fourrmtech.app.models.InquiryListResponse;
import com.fourrmtech.app.models.InquiryRequest;
import com.fourrmtech.app.models.InquiryRequestResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("products.php")
    Call<ProductResponse> getProducts();

    @GET("products.php")
    Call<ProductSingleResponse> getProductById(
            @Query("id") int productId
    );

    @POST("register.php")
    Call<RegisterResponse> register(
            @Body RegisterRequest request
    );

    @POST("login.php")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("get_customer.php")
    Call<CustomerResponse> getCustomerById(
            @Query("id") int customerId
    );

    @GET("get_customers.php")
    Call<CustomerListResponse> getCustomers();

    @GET("get_appointments.php")
    Call<AppointmentResponse> getAppointments(
            @Query("customer_id") int customerId
    );

    @GET("get_all_appointments.php")
    Call<AppointmentResponse> getAllAppointments();

    @POST("create_appointment.php")
    Call<CreateAppointmentResponse> createAppointment(
            @Body CreateAppointmentRequest request
    );

    @POST("cancel_appointment.php")
    Call<CreateAppointmentResponse> cancelAppointment(
            @Body CancelAppointmentRequest request
    );

    @POST("update_appointment_status.php")
    Call<CreateAppointmentResponse> updateAppointmentStatus(
            @Body UpdateAppointmentStatusRequest request
    );

    @POST("update_customer.php")
    Call<CreateAppointmentResponse> updateCustomer(
            @Body UpdateCustomerRequest request
    );

    @POST("update_category.php")
    Call<CreateAppointmentResponse> updateCategory(
            @Body UpdateCategoryRequest request
    );

    @POST("delete_category.php")
    Call<CreateAppointmentResponse> deleteCategory(
            @Body DeleteCategoryRequest request
    );

    @POST("admin_login.php")
    Call<AdminLoginResponse> adminLogin(
            @Body AdminLoginRequest request
    );

    @POST("add_product.php")
    Call<ProductSingleResponse> addProduct(
            @Body AddProductRequest request
    );

    @POST("update_product.php")
    Call<ProductSingleResponse> updateProduct(
            @Body UpdateProductRequest request
    );

    @POST("delete_product.php")
    Call<CreateAppointmentResponse> deleteProduct(
            @Body DeleteProductRequest request
    );

    @GET("get_categories.php")
    Call<CategoryResponse> getCategories();

    @POST("add_category.php")
    Call<CategoryResponse> addCategory(
            @Body AddCategoryRequest request
    );

    @POST("submit_inquiry.php")
    Call<InquiryRequestResponse> submitInquiry(
            @Body InquiryRequest request
    );

    @POST("request_password_reset.php")
    Call<ForgotPasswordResponse> requestPasswordReset(
            @Body ForgotPasswordRequest request
    );

    @GET("get_all_inquiries.php")
    Call<InquiryListResponse> getAllInquiries();
    @POST("verify_password_reset.php")
    Call<ForgotPasswordResponse> verifyPasswordReset(@Body VerifyResetRequest request);

    @POST("reset_password.php")
    Call<ForgotPasswordResponse> finishPasswordReset(@Body FinishResetRequest request);

}