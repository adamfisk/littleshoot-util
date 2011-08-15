package org.littleshoot.util;

/**
 * App-wide constants.
 */
public class ShootConstants {
    
    public static final String SECURE_SERVER_BASE = "https://littleshoot.org";

    public static final String BASE_DOMAIN = "www.littleshoot.org";//"localhost:8080";//"littleshootapi.appspot.com"; //"www.littleshoot.org";
    
    /**
     * The base base URL for the server.
     */
    public static final String SERVER_BASE = "http://" + BASE_DOMAIN;
    
    /**
     * The URL of the server that manages the Little Shoot resource repository.
     */
    public static final String SERVER_URL = SERVER_BASE; 
    
    /**
     * URL for the bugs server.
     */
    public static final String BUGS_URL = "http://littleshootbugs.appspot.com/submit";
    
    /**
     * The port we run HTTP over for the API.
     */
    public static final int API_PORT = 8107;
    
    /**
     * The port we allow file requests over.
     */
    public static final int FILES_PORT = 8108;

    /**
     * The name of the world group.
     */
    public static final String WORLD_GROUP = "world";

    public static final String HII_KEY = "hii.key";
    
    public static final String HII = "hii";
    
}
