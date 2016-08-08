package ua.gov.dp.econtact.manager;

import android.content.Context;

import ua.gov.dp.econtact.util.CachedValue;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Dmitriy Dovbnya
 */
//CHECKSTYLE:OFF
public class SharedPrefManager implements Manager {

    private static final String NAME = "sharedPrefs";
    private static final String API_KEY = "api_key";
    private static final String OPEN_PROFILE_EDIT_SCREEN = "open_fill_user_info_screen";
    private static final String CATEGORIES = "categories";

    private static final String ID = "USER_ID";
    private static final String FB_USER_ID = "FB_USER_ID";

    private Set<CachedValue> mCachedValues;

    private CachedValue<String> mApiKey;
    private CachedValue<Long> mUserId;

    private CachedValue<Boolean> mOpenUserInfoFillingScreen;
    private CachedValue<Set<Long>> mCategoriesId;
    private CachedValue<String> mFbUserId;

    @Override
    public void init(Context context) {
        CachedValue.initialize(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
        mCachedValues = new HashSet<>();
        mCachedValues.add(mApiKey = new CachedValue<>(API_KEY, String.class));
        mCachedValues.add(mOpenUserInfoFillingScreen = new CachedValue<>(OPEN_PROFILE_EDIT_SCREEN, false, Boolean.class));
        mCachedValues.add(mUserId = new CachedValue<>(ID, 0L, Long.class));
        mCachedValues.add(mCategoriesId = new CachedValue<>(CATEGORIES, Set.class));
        mCachedValues.add(mFbUserId = new CachedValue<>(FB_USER_ID, String.class));
    }

    public void setApiKey(final String apiKey) {
        mApiKey.setValue(apiKey);
    }

    public String getApiKey() {
        return mApiKey.getValue();
    }

    public Long getUserId() {
        return mUserId.getValue();
    }

    public void setTokenSent(boolean isSent) {
        //TODO implement
    }

    void setUserId(Long userId) {
        mUserId.setValue(userId);
    }

    public void setOpenUserInfoFillingScreen(final boolean openUserInfoFillingScreen) {
        this.mOpenUserInfoFillingScreen.setValue(openUserInfoFillingScreen);
    }

    public void setCategoriesId(Set<Long> categoriesId) {
        this.mCategoriesId.setValue(categoriesId);
    }

    public Set<Long> getCategoriesId() {
        return this.mCategoriesId.getValue() == null ? new LinkedHashSet<Long>() : this.mCategoriesId.getValue();
    }

    public boolean getOpenUserInfoFillingScreen() {
        return mOpenUserInfoFillingScreen.getValue();
    }

    public void setFbUserId(final String fbUserId) {
        this.mFbUserId.setValue(fbUserId);
    }

    public String getFbUserId() {
        return mFbUserId.getValue();
    }

    @Override
    public void clear() {
        for (CachedValue value : mCachedValues) {
            value.delete();
        }
    }

    public void clearId() {
        mUserId.setValue(0L);
    }

    public void clearFbId() {
        mFbUserId.setValue("");
    }
}
//CHECKSTYLE:ON
