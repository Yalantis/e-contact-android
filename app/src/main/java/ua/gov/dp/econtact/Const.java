package ua.gov.dp.econtact;

import java.util.Arrays;
import java.util.List;

import ua.gov.dp.econtact.api.ApiSettings;

/**
 * Created by Yalantis
 */
public final class Const {

    private Const() {
    }
    public static final int MILLIS_IN_SECOND = 1000;
    public static final int AVATAR_SIZE = 320;
    public static final String RU = "ru";
    public static final int MIN_PASS_LENGTH = 4;
    /**
     * REQUESTS ACTIVITIES CODES
     */
    public final static int REQUEST_CODE_AUTH = 22;

    public static final int DEFAULT_OFFSET = 0;
    public static final int DEFAULT_AMOUNT = 20;

    public final static String BLURRED_BACKGROUND_FILE_NAME = "blurred_background.png";
    public final static String URL_IMAGE = ApiSettings.SCHEME
            + (BuildConfig.IS_PROD ? ApiSettings.HOSTNAME_PROD : ApiSettings.HOSTNAME_DEV)
            + "/files/ticket/";

    public static final int TICKET_STATUS_DRAFT = 123;
    public static final int TICKET_STATUS_MODERATION = 11;
    public static final int TICKET_STATUS_REJECTED = 2;

    public static final List<String> FB_PERMISSION = Arrays.asList("email", "public_profile", "user_friends");

    public static final String FIELD_CATEGORY = "category";

    public final class Realm {
        private Realm() {
        }

        public static final String STORAGE_DEFAULT = "default_storage.realm";
        public static final long SCHEMA_VERSION = 1;


    }

    public final class DefaultLocation {
        private DefaultLocation() {
        }

        public static final double DEFAULT_AREA_LATITUDE = 48.3376975;
        public static final double DEFAULT_AREA_LONGITUDE = 34.9479825;
    }

    public final class Data {
        private Data() {
        }

        public static final int IMAGE = 1;
        public static final int VIDEO = 2;
    }

    public final class IntentConstant {
        private IntentConstant() {
        }

        public static final String MAX_QUANTITY = "max_photo";
        public static final String MIN_QUANTITY = "min_photo";
        public static final String LIST_OF_PATHS = "paths";
        public static final String GALLERY_TYPE = "gal_type";
        public static final String PORTRAIT = "portrait";
    }

    public final class GalleryType {
        private GalleryType() {
        }

        public static final int IMAGE = 0;
        public static final int VIDEO = 1;
        public static final int IMAGE_VIDEO = 2;
    }

    public class ApiCode {
        public final static int CODE_INCORRECT_OLD_PASSWORD = 403;
        public final static int CODE_LOGIN_FAIL = 404;
        public final static int CODE_TOO_MANY_REQUESTS = 429;
        public final static int CODE_BAD_REQUEST = 400;
    }
}
