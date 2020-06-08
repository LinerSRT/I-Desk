package com.liner.i_desk;

import java.util.Random;

public class Constants {
    /**
     * Firebase
     */
    public static final String USERS_DATABASE_KEY = "Users";
    public static final String REQUESTS_DATABASE_KEY = "Requests";
    public static final String MESSAGES_DATABASE_KEY = "Messages";
    public static final String FILES_DATABASE_KEY = "Files";

    /**
     * App
     */
    public static final int GENERATED_ID_LENGTH = 16;
    public static final boolean USERS_REQUIRE_EMAIL_VERIFICATION = false;
    public static final int USER_STATUS_UPDATE_PERIOD = 2;
    public static final long USER_STORAGE_SIZE = 67108864;

}
