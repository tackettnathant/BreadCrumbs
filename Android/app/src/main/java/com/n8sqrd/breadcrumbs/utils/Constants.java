package com.n8sqrd.breadcrumbs.utils;

/**
 * Created by ntackett on 1/4/2018.
 */

public class Constants {
    public static final String TAG = "BREADCRUMBS_TAG";
    public static final String DB_NAME = "com.n8sqrd.breadcrumbs.db";
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String PATH_NAME_DATE = "EEE, MMM d, yyyy 'at' h:m:s a";
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int MAX_ADDRESS_RESULTS = 5;
    public static final String PACKAGE_NAME =
            "com.n8sqrd.breadcrumbs";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final int PROMPT_CONTEXT_ALL=0;
    public static final float MIN_MOVE_DISTANCE=50; //Minimum distance in meters to trigger a location update
}
