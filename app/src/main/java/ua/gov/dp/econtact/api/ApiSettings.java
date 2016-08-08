package ua.gov.dp.econtact.api;

import ua.gov.dp.econtact.BuildConfig;

public final class ApiSettings {

    private ApiSettings() {
    }

    public static final String HOSTNAME_DEV = "dev-contact.yalantis.com";
    public static final String HOSTNAME_PROD = "e-contact.yalantis.com";
    // public static final String HOSTNAME_DEV = "178.62.188.48";
    // public static final String HOSTNAME_DEV = "178.62.188.48.xip.io";

    public static final String HOSTNAME = BuildConfig.IS_PROD ? HOSTNAME_PROD : HOSTNAME_DEV;
    public static final String SCHEME = "http://";

    public static final String API_PREFIX = "/rest";

    public static final String API_VERSION = "/v1";
    public static final String SERVER = SCHEME + (BuildConfig.IS_PROD ? HOSTNAME_PROD : HOSTNAME_DEV) + API_PREFIX + API_VERSION;

    public static final String SERVER_PROD = SCHEME + HOSTNAME_PROD + API_PREFIX + API_VERSION;

    public static final String AUTH_TOKEN = "token";

    public static final String ANSWER_FILE = SCHEME + (BuildConfig.IS_PROD ? HOSTNAME_PROD : HOSTNAME_DEV) + "/files/answers/";

    public static final class URL {
        public static final String USER = "/user";
        public static final String USERS = "/users";
        public static final String USER_BY_ID = USER + "/{id}";
        public static final String AUTH = "/user-auth-check";
        public static final String REGISTER = "/user-register";
        public static final String VALIDATE = "/user/validate";

        public static final String TICKETS = "/tickets";
        public static final String STAT = "/stat";
        public static final String STAT_ALL = STAT + "/all";
        public static final String VOCABULARY = "/vocabulary";
        public static final String ADDRESS_BOOK = "/address-book";
        public static final String CATEGORY_ALL = "/ticket-categories";
        public static final String TICKET = "/ticket";
        public static final String TICKET_ID = "ticket_id";
        public static final String DISTRICT_ALL = ADDRESS_BOOK + "/districts";
        public static final String CITIES_ALL = ADDRESS_BOOK + "/cities";
        public static final String STREETS_ALL = ADDRESS_BOOK + "/streets";
        public static final String HOUSES_ALL = ADDRESS_BOOK + "/houses";
        public static final String MY_TICKETS = "/my-tickets";
        public static final String LIKE = TICKET + "/{ticket_id}/like";
        public static final String UPDATE_TOKEN = "/update-token";
        public static final String RESET_PASSWORD = "/reset-password";
        public static final String TICKETS_BY_IDS = "/tickets-by-ids";
        public static final String LOGOUT = "/logout";
        public static final String CHANGE_PASSWORD = "/change-password";
        public static final String GET_MY_TICKET_BY_ID = "/my-ticket/"+"{"+TICKET_ID+"}";
    }

    public static final class USER {
        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final int DEVICE_TYPE = 2;
    }
}
