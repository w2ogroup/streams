package org.apache.streams.data.util;

import org.apache.streams.pojo.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing activities
 */
public class ActivityUtil {
    private ActivityUtil() {}

    public static final String EXTENSION_PROPERTY = "extensions";
    public static final String LIKES_EXTENSION = "likes";
    public static final String REBROADCAST_EXTENSION = "rebroadcasts";
    public static final String LOCATION_EXTENSION = "location";
    public static final String LOCATION_EXTENSION_COUNTRY = "country";
    public static final String LOCATION_EXTENSION_COORDINATES = "coordinates";

    /**
     * Creates a standard extension property
     * @param activity activity to create the property in
     * @return the Map representing the extensions property
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> ensureExtensions(Activity activity) {
        Map<String, Object> properties = (Map)activity.getAdditionalProperties().get(EXTENSION_PROPERTY);
        if(properties == null) {
            properties = new HashMap<String, Object>();
            activity.setAdditionalProperties(EXTENSION_PROPERTY, properties);
        }
        return properties;
    }

    /**
     * Gets a formatted ID
     * @param providerName name of the provider
     * @param personId ID of the person within the system
     * @return id:<providerName>:people:<personId>
     */
    public static String getPersonId(String providerName, String personId) {
        return String.format("id:%s:people:%s", providerName, personId);
    }

    /**
     * Gets a formatted provider ID
     * @param providerName name of the provider
     * @return id:providers:<providerName>
     */
    public static String getProviderId(String providerName) {
        return String.format("id:providers:%s", providerName);
    }

    /**
     * Gets a formatted object ID
     * @param provider name of the provider
     * @param objectType type of the object
     * @param objectId the ID of the object
     * @return id:<provider>:<objectType>s:<objectId>
     */
    public static String getObjectId(String provider, String objectType, String objectId) {
        return String.format("id:%s:%ss:%s", provider, objectType, objectId);
    }

}
