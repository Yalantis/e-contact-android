package ua.gov.dp.econtact.api.request;

import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.dto.ChangePasswordDTO;
import ua.gov.dp.econtact.model.dto.GetUserDTO;
import ua.gov.dp.econtact.model.dto.PasswordResetDTO;
import ua.gov.dp.econtact.model.dto.PushTokenDto;

public interface UserApi {

    @GET(ApiSettings.URL.USER_BY_ID)
    void getUser(@Path(ApiSettings.USER.ID) long id,
                 Callback<User> callback);

    @GET(ApiSettings.URL.USERS)
    void getUsers(Callback<GetUserDTO> callback);

    @PUT(ApiSettings.URL.USER_BY_ID)
    void updateUser(@Path(ApiSettings.USER.ID) long id,
                    @Body JsonElement user, Callback<Void> callback);

    @POST(ApiSettings.URL.AUTH)
    void signIn(@Body JsonElement authDto, Callback<GetUserDTO> callback);

    @POST(ApiSettings.URL.REGISTER)
    void register(@Body JsonElement userDto, Callback<GetUserDTO> callback);

    @POST(ApiSettings.URL.VALIDATE)
    void validate(@Body JsonElement userDto, Callback<GetUserDTO> callback);

    @DELETE(ApiSettings.URL.USER_BY_ID)
    void deleteUserById(@Path(ApiSettings.USER.ID) long id,
                        Callback<GetUserDTO> callback);

    @PUT(ApiSettings.URL.UPDATE_TOKEN)
    void updateToken(@Body PushTokenDto pushTokenBody, Callback<Void> callback);

    @PUT(ApiSettings.URL.RESET_PASSWORD)
    void resetPassword(@Body PasswordResetDTO passwordResetDTO, Callback<Void> callback);

    @PUT(ApiSettings.URL.LOGOUT)
    void logout(Callback<Void> callback);

    @PUT(ApiSettings.URL.CHANGE_PASSWORD)
    void changePassword(@Body ChangePasswordDTO changePasswordDTO, Callback<Void> callback);
}
