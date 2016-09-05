package scn.com.sipclient.net;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import scn.com.sipclient.model.ContactItem;

/**
 * Created by star on 6/16/2016.
 */
public interface SipAPI {
    @POST("users/signin")
    Call<JsonObject> signin(@Query("email") String email, @Query("password") String password);

    @FormUrlEncoded
    @POST("users/signout")
    Call<JsonObject> signout(@Field("TOKEN") String token);


    @FormUrlEncoded
    @POST("users/signup")
    Call<JsonObject> signup(@Field("UserName") String username , @Field("UserEmail") String email, @Field("Password") String password);

    @FormUrlEncoded
    @POST("user/send_reset_password")
    Call<JsonObject> forget_password(@Field("user_email") String email);

    @FormUrlEncoded
    @POST("user/reset_password")
    Call<JsonObject> reset_password(@Field("login_id") String login_id , @Field("activate_key") String activate_key ,@Field("password") String password);

    @FormUrlEncoded
    @POST("user/change_password")
    Call<JsonObject> change_password(@Field("user_id") int user_id , @Field("old_password") String old_password ,@Field("new_password") String new_password);

    @POST("users/search/{email}")
    Call<JsonObject> user_search(@Query("email") String email);

    @GET("users/{id}/favorites")
    Call<JsonObject> favorite_search(@Path("id") int id);

    @GET("users/{id}/contacts/{name}")
    Call<JsonObject> contact_search(@Path("id") int id, @Path("name") String name);

    @FormUrlEncoded
    @POST("users/{id}/contacts")
    Call<JsonObject> add_contact(@Path("id") int id, @Field("Email") String email , @Field("Name") String name);

    @FormUrlEncoded
    @PUT("users/{id}/contacts/{contactId}")
    Call<JsonObject> update_contact(@Path("id") int id,@Path("contactId") int contactId, @Field("Email") String email , @Field("Name") String name);

    @DELETE("users/{id}/contacts/{contactId}")
    Call<JsonObject> delete_contact(@Path("id") int id,@Path("contactId") int contactId );


    @FormUrlEncoded
    @POST("users/{id}/favorites")
    Call<JsonObject> add_favorite(@Path("id") int id, @Field("Email") String email , @Field("Name") String name);

    @DELETE("users/{id}/favorites/{favorId}")
    Call<JsonObject> delete_favorite(@Path("id") int id,@Path("favorId") int favorId );


    @GET("users/{id}/history")
    Call<JsonObject> history_search(@Path("id") int id);

    @FormUrlEncoded
    @POST("users/{id}/history")
    Call<JsonObject> add_history(@Path("id") int id, @Field("CallerEmail") String email , @Field("CallStatus") int status,@Field("CallSummary") String summary, @Field("CallDate") String date);


    @GET("values/{id}")
    Call<JsonArray> user_get(@Path("id") int user_id);

    @FormUrlEncoded
    @POST("user/get_sessioninfo")
    Call<JsonObject> get_sessioninfo(@Field("TOKEN") String token);

    @FormUrlEncoded
    @POST("user/activate_user")
    Call<JsonObject> user_activate(@Field("login_id") String login_id, @Field("activate_key") String activate_key);

}
