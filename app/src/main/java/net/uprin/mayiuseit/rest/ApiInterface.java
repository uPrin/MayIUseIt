package net.uprin.mayiuseit.rest;

import net.uprin.mayiuseit.model.AccessToken;
import net.uprin.mayiuseit.model.DocumentListResponse;
import net.uprin.mayiuseit.model.DocumentResponse;
import net.uprin.mayiuseit.model.JoinRequest;
import net.uprin.mayiuseit.model.JoinResponse;
import net.uprin.mayiuseit.model.LoginResponse;
import net.uprin.mayiuseit.model.NoticeListResponse;
import net.uprin.mayiuseit.model.SearchListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by uPrin on 2017. 11. 1..
 */

public interface ApiInterface {
    @POST("join.php")
    Call<JoinResponse> postEmailJoin (@Body JoinRequest joinRequest);
    @POST("login.php")
    Call<LoginResponse> postEmailLogin (@Body JoinRequest joinRequest);
    @GET("list.php")
    Call<DocumentListResponse> getDocumentList(@Query("pageNum") int pageNum, @Query("category") int category, @Query("rankBy") String rankBy);
    @GET("document_token.php")
    Call<DocumentResponse> getDocument(@Query("document_srl") int document_srl);
    @GET("notice.php")
    Call<NoticeListResponse> getNotice(@Query("pageNum") int pageNum);

    @POST("refreshToken.php")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("REFRESH_TOKEN") String refreshToken);

    @POST("token_validate.php")
    @FormUrlEncoded
    Call<ApiError> validToken(@Field("ACCESS_TOKEN") String accessToken);

    @POST("token.php")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("social_auth.php")
    @FormUrlEncoded
    Call<AccessToken> socialAuth(@Field("name") String name,
                                 @Field("email") String email,
                                 @Field("provider") String provider,
                                 @Field("provider_user_id") String providerUserId);

    @POST("facebook_auth.php")
    @FormUrlEncoded
    Call<AccessToken> facebook_auth(@Field("ACCESS_TOKEN") String accessToken);

    @GET("search.php")
    Call<SearchListResponse> getSearchList(@Query("pageNum") int pageNum, @Query("keyword") String keyword, @Query("category") int category, @Query("rankBy") String rankBy);
}
