package ua.gov.dp.econtact.manager;

import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidApacheClient;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.BuildConfig;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.api.MainExecutor;
import ua.gov.dp.econtact.api.request.AddressApi;
import ua.gov.dp.econtact.api.request.CategoryApi;
import ua.gov.dp.econtact.api.request.StatApi;
import ua.gov.dp.econtact.api.request.TicketApi;
import ua.gov.dp.econtact.api.request.UserApi;
import ua.gov.dp.econtact.api.serializer.AddressSerializer;
import ua.gov.dp.econtact.api.serializer.CategorySerializer;
import ua.gov.dp.econtact.api.serializer.CityDistrictSerializer;
import ua.gov.dp.econtact.api.serializer.CitySerializer;
import ua.gov.dp.econtact.api.serializer.DistrictSerializer;
import ua.gov.dp.econtact.api.serializer.FacilitiesSerializer;
import ua.gov.dp.econtact.api.serializer.HouseSerializer;
import ua.gov.dp.econtact.api.serializer.SocialConditionSerializer;
import ua.gov.dp.econtact.api.serializer.StateSerializer;
import ua.gov.dp.econtact.api.serializer.StreetSerializer;
import ua.gov.dp.econtact.api.serializer.TypeSerializer;
import ua.gov.dp.econtact.api.serializer.UserSerializer;
import ua.gov.dp.econtact.api.task.ChangePasswordTask;
import ua.gov.dp.econtact.api.task.DeleteUserTask;
import ua.gov.dp.econtact.api.task.GetTicketByIdTask;
import ua.gov.dp.econtact.api.task.GetTicketsByIdsTask;
import ua.gov.dp.econtact.api.task.GetUserTask;
import ua.gov.dp.econtact.api.task.LogoutTask;
import ua.gov.dp.econtact.api.task.PasswordResetTask;
import ua.gov.dp.econtact.api.task.RegisterUserTask;
import ua.gov.dp.econtact.api.task.SignInTask;
import ua.gov.dp.econtact.api.task.UpdateTokenTask;
import ua.gov.dp.econtact.api.task.UpdateUserTask;
import ua.gov.dp.econtact.api.task.ValidateUserTask;
import ua.gov.dp.econtact.api.task.address.GetCitiesTask;
import ua.gov.dp.econtact.api.task.address.GetDistrictsTask;
import ua.gov.dp.econtact.api.task.address.GetHouseTask;
import ua.gov.dp.econtact.api.task.address.GetStreetTask;
import ua.gov.dp.econtact.api.task.category.GetAllCategoryTask;
import ua.gov.dp.econtact.api.task.stat.GetAllStatTask;
import ua.gov.dp.econtact.api.task.ticket.CreateTicketTask;
import ua.gov.dp.econtact.api.task.ticket.GetMyTicketByIdTask;
import ua.gov.dp.econtact.api.task.ticket.GetSmallTicketsTask;
import ua.gov.dp.econtact.api.task.ticket.GetTicketsTask;
import ua.gov.dp.econtact.api.task.ticket.LikeTask;
import ua.gov.dp.econtact.api.task.ticket.UploadPhotoTask;
import ua.gov.dp.econtact.model.Facilities;
import ua.gov.dp.econtact.model.SocialCondition;
import ua.gov.dp.econtact.model.State;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.CityDistrict;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;
import ua.gov.dp.econtact.model.category.Category;
import ua.gov.dp.econtact.model.dto.CreateTicketDTO;

public class ApiManager implements Manager {

    private Context mContext;
    private MainExecutor mExecutor;

    private UserApi mUserApi;
    private TicketApi mTicketApi;
    private StatApi mStatApi;
    private CategoryApi mCategoryApi;

    private RestAdapter mRestAdapter;
    private AccountManager mAccountManager;
    private AddressApi mDistrictApi;

    @Override
    public void init(final Context context) {
        mContext = context;
        mExecutor = new MainExecutor();
        mAccountManager = AccountManager.get(context);
        initRestModules();
        setApi();
        getDistricts();
    }

    public void getTicketById(long ticketId) {
        mExecutor.execute(new GetTicketByIdTask(mTicketApi, ticketId));
    }

    public void getMyTicketById(long ticketId) {
        mExecutor.execute(new GetMyTicketByIdTask(mTicketApi, ticketId));
    }

    public void resetPassword(String email) {
        mExecutor.execute(new PasswordResetTask(mUserApi, email));
    }

    public void getTicketsByIds(String ids) {
        mExecutor.execute(new GetTicketsByIdsTask(mTicketApi, ids));
    }

    private void initRestModules() {
        try {
            mRestAdapter = new RestAdapter.Builder()
                    .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                    .setEndpoint(ApiSettings.SERVER)
                    .setClient(new AndroidApacheClient())
                    .setConverter(createConverter())
                    .setRequestInterceptor(createRequestInterceptor())
                    .build();
        } catch (ClassNotFoundException e) {
            Timber.e(e,"Error while initializing rest adapter");
        }
    }

    public void getCities(final long districtId) {
        mExecutor.execute(new GetCitiesTask(mDistrictApi, districtId));
    }

    public void getStreets(final long cityId) {
        mExecutor.execute(new GetStreetTask(mDistrictApi, cityId));
    }

    public void getHouses(final long streetId) {
        mExecutor.execute(new GetHouseTask(mDistrictApi, streetId));
    }

    public void getDistricts() {
        mExecutor.execute(new GetDistrictsTask(mDistrictApi));
    }

    public void getUser(final long userId) {
        mExecutor.execute(new GetUserTask(mUserApi, userId));
    }

    public void updateUser(final User user) {
        mExecutor.execute(new UpdateUserTask(mUserApi, user));
    }

    public void login(final String email, final String password, final SignInTask.LoginCallback callback) {
        mExecutor.execute(new SignInTask(mUserApi, mAccountManager, email, password, callback));
    }

    public void register(final String email, final String firstName, final String lastName,
                         final String middleName, final String password, final Address address,
                         final String phone, final RegisterUserTask.RegisterSuccessfulCallback callback) {
        mExecutor.execute(new RegisterUserTask(mUserApi, email, firstName, lastName, middleName,
                password, address, phone, mAccountManager, callback));
    }

    public void validate(final String email, final String firstName, final String lastName,
                         final String middleName, final String password, final Address address,
                         final ValidateUserTask.ValidateCallback callback) {
        mExecutor.execute(new ValidateUserTask(mUserApi, email, firstName, lastName, middleName,
                password, address, mAccountManager, callback));
    }

    public void logout() {
        mExecutor.execute(new LogoutTask(mUserApi));
    }

    private RequestInterceptor createRequestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void intercept(final RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("Accept", "application/json");
                if (!TextUtils.isEmpty(App.accountManager.getAuthToken())) {
                    request.addHeader("Authorization", "Bearer " + App.accountManager.getAuthToken());
                }
            }
        };
    }

    private Converter createConverter() throws ClassNotFoundException {
        return new GsonConverter(getGsonBuilder().create());
    }

    private void setApi() {
        this.mUserApi = mRestAdapter.create(UserApi.class);
        this.mTicketApi = mRestAdapter.create(TicketApi.class);
        this.mStatApi = mRestAdapter.create(StatApi.class);
        this.mCategoryApi = mRestAdapter.create(CategoryApi.class);
        this.mDistrictApi = mRestAdapter.create(AddressApi.class);
    }

    public GsonBuilder getGsonBuilder() throws ClassNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return false;
            }
        });
        builder.registerTypeAdapter(getRealmType(User.class.getSimpleName()), new UserSerializer());
        builder.registerTypeAdapter(getRealmType(Address.class.getSimpleName()), new AddressSerializer());
        builder.registerTypeAdapter(getRealmType(City.class.getSimpleName()), new CitySerializer());
        builder.registerTypeAdapter(getRealmType(District.class.getSimpleName()), new DistrictSerializer());
        builder.registerTypeAdapter(getRealmType(House.class.getSimpleName()), new HouseSerializer());
        builder.registerTypeAdapter(getRealmType(Street.class.getSimpleName()), new StreetSerializer());
        builder.registerTypeAdapter(getRealmType(State.class.getSimpleName()), new StateSerializer());
        builder.registerTypeAdapter(getRealmType(Facilities.class.getSimpleName()), new FacilitiesSerializer());
        builder.registerTypeAdapter(getRealmType(SocialCondition.class.getSimpleName()), new SocialConditionSerializer());
        builder.registerTypeAdapter(getRealmType(Category.class.getSimpleName()), new CategorySerializer());
        builder.registerTypeAdapter(getRealmType(ua.gov.dp.econtact.model.Type.class.getSimpleName()), new TypeSerializer());
        builder.registerTypeAdapter(getRealmType(CityDistrict.class.getSimpleName()), new CityDistrictSerializer());
        return builder;
    }

    private java.lang.reflect.Type getRealmType(final String className) throws ClassNotFoundException {
        return Class.forName("io.realm." + className + "RealmProxy");
    }

    @Override
    public void clear() {
        mExecutor.clear();
    }


    public void deleteUserById(final long userId) {
        mExecutor.execute(new DeleteUserTask(mUserApi, userId));
    }

    public void getStatAll() {
        mExecutor.execute(new GetAllStatTask(mStatApi));
    }

    public void getTickets(final int offset, final int amount) {
        mExecutor.execute(new GetTicketsTask(mTicketApi, offset, amount));
    }

    public void getTicketsForMap() {
        mExecutor.execute(new GetSmallTicketsTask(mTicketApi));
    }

    public void getTicketsByState(final TicketStates state, final int offset, final int amount) {
        mExecutor.execute(new GetTicketsTask(mTicketApi, offset, amount, state));
    }

    public void getTicketsByStateAndCategory(final TicketStates state, final int offset,
                                             final int amount, final Long[] categories) {
        mExecutor.execute(new GetTicketsTask(mTicketApi, offset, amount, state, categories));
    }

    public void getCategoriesAll() {
        mExecutor.execute(new GetAllCategoryTask(mCategoryApi));
    }

    public void createTicket(final CreateTicketDTO createTicketDTO) {
        mExecutor.execute(new CreateTicketTask(mTicketApi, createTicketDTO));
    }

    public void uploadPhoto(final long ticketId, final String filePath) {
        mExecutor.execute(MainExecutor.QueueType.NO_QUEUE, new UploadPhotoTask(mContext, filePath, ticketId));
    }

    public void likeTicket(final long ticketId, final String fbToken) {
        mExecutor.execute(new LikeTask(mTicketApi, ticketId, fbToken));
    }

    public void updateToken(String token, UpdateTokenTask.Callback callback) {
        mExecutor.execute(new UpdateTokenTask(mUserApi, token, callback));
    }

    public void changePassword(String oldPassword, String newPassword, ChangePasswordTask.Callback changePasswordCallback) {
        mExecutor.execute(new ChangePasswordTask(mUserApi, oldPassword, newPassword, changePasswordCallback));
    }
}
