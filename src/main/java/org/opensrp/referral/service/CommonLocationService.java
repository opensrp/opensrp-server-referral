package org.opensrp.referral.service;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import com.squareup.okhttp.*;
import org.apache.ibatis.jdbc.Null;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.common.util.HttpUtil;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommonLocationService extends OpenmrsLocationService {

    private static Logger logger = LoggerFactory.getLogger(OpenmrsLocationService.class);

    public CommonLocationService(String openmrsUrl, String user, String password) {
        super(openmrsUrl, user, password);
    }

    public List<Location> getLocationsWithinAHierarchyLevel(String uuid, String locationTopLevel, JSONArray locationHierarchy, JSONArray locationTagsQueried) throws JSONException {
        List<Location> allLocationsList = new ArrayList<>();
        allLocationsList = getAllLocations(allLocationsList,0);
        String locationsJson = new Gson().toJson(allLocationsList);
        logger.info(locationsJson);
        return getLocationsByTagsAndHierarchyLevel(uuid, allLocationsList,locationTopLevel,locationHierarchy,locationTagsQueried);
    }

    public List<Location> getAllLocations(List<Location> locationList, int startIndex) throws JSONException {
        String response = this.obtainURL(HttpUtil.removeEndingSlash(this.OPENMRS_BASE_URL) + "/" + "ws/rest/v1/location" +
                "?v=custom:(uuid,display,name,tags:(uuid,display),parentLocation:(uuid,display),attributes)&limit=100&startIndex="+startIndex);
        logger.info("response received : {} ", response);
        if (!StringUtils.isEmptyOrWhitespaceOnly(response) && (new JSONObject(response)).has("results")) {
            JSONArray results = new JSONObject(response).getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                locationList.add(formLocation(results.getJSONObject(i).toString()));
            }
            return getAllLocations(locationList,startIndex+100);

        }
        return  locationList;
    }

    private String obtainURL(String url) {
        Request request = (new Request.Builder()).url(url).addHeader("Authorization", Credentials.basic(this.OPENMRS_USER, this.OPENMRS_PWD)).build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            String responseBody = response.body().string();
            if (!StringUtils.isEmptyOrWhitespaceOnly(responseBody)) {
                return responseBody;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private Location formLocation(String locationJson) throws JSONException {
        logger.info("makeLocation: {}", locationJson);
        JSONObject obj = new JSONObject(locationJson);
        Location parentLocation = this.getParent(obj);
        Location location = new Location(obj.getString("uuid"), obj.getString("name"),
                null, null, parentLocation, null, null);
        JSONArray tags = obj.getJSONArray("tags");

        for (int i = 0; i < tags.length(); ++i) {
            location.addTag(tags.getJSONObject(i).getString("display"));
        }

        JSONArray attributes = obj.getJSONArray("attributes");

        for (int i = 0; i < attributes.length(); ++i) {
            boolean voided = attributes.getJSONObject(i).optBoolean("voided");
            if (!voided) {
                String ad = attributes.getJSONObject(i).getString("display");
                location.addAttribute(ad.substring(0, ad.indexOf(':')), ad.substring(ad.indexOf(':') + 2));
            }
        }
        return location;
    }

    /**
     * This method is used to obtain locations within a hierarchy level by passing the following parameters
     * @param uuid a uuid of a location within the hierarchy level, this is used for transversing to the top location level stated below
     * @param allLocations this is a list of all locations obtained from OpenMRS
     * @param locationTopLevel this defines the top most level that locations to be querried from
     *                         e.g
     *                         1. for obtaining all locations within a district this value would contain the tag name
     *                         district locations
     *                         2. for obtaining all locations within a region this value would contain the tag name for
     *                         region locations
     *
     * @param locationHierarchy this defines all the tags used in a specific implementations location hierarchy
     * @param locationTagsQueried this defines the tags of all the locations to be returned
     *                            e.g for obtaining all villages this json array would contain the tag name for village locations
     *                            for villages and health facilities, this json array would contain both tag names
     * @return returns a list of all locations matching the above criteria
     */
    public List<Location> getLocationsByTagsAndHierarchyLevel(String uuid, List<Location> allLocations, String locationTopLevel, JSONArray locationHierarchy, JSONArray locationTagsQueried) {
        List<Location> filteredList = new ArrayList<>();
        for (Location allLocation : allLocations) {
            if (allLocation.getLocationId().contains(uuid)) {
                filteredList.add(allLocation);
            }
        }

        Location location;

        if (!filteredList.isEmpty()) {
            location = filteredList.get(0);
        } else {
            return new ArrayList<>();
        }

        for(int i=0;i<locationHierarchy.length();i++){
            try {
                if (location.getTags().contains(locationHierarchy.getString(i)) && !locationHierarchy.getString(i).equals(locationTopLevel)) {
                    return getLocationsByTagsAndHierarchyLevel(location.getParentLocation().getLocationId(), allLocations,locationTopLevel,locationHierarchy,locationTagsQueried);
                }
            } catch (JSONException e) {
                logger.error(e.getMessage());
            }
        }

        if (location.getTags().contains(locationTopLevel)) {
            return getChildLocationsTreeByTagsAndParentLocationUUID(location.getLocationId(), allLocations,locationTagsQueried);
        } else {
            return new ArrayList<>();
        }
    }


    private List<Location> getChildLocationsTreeByTagsAndParentLocationUUID(String parentUUID, List<Location> allLocations,JSONArray locationTagsQueried) {
        List<Location> obtainedLocations = new ArrayList<>();
        for (Location location : allLocations) {
            for(int i=0;i<locationTagsQueried.length();i++){
                try {
                    if (location.getParentLocation().getLocationId().equals(parentUUID) && location.getTags().contains(locationTagsQueried.getString(i))) {
                        obtainedLocations.add(location);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            try {
                if (location.getParentLocation().getLocationId().equals(parentUUID)) {
                    obtainedLocations.addAll(getChildLocationsTreeByTagsAndParentLocationUUID(location.getLocationId(), allLocations, locationTagsQueried));
                }
            }catch (NullPointerException e){
                logger.error(e.getMessage());
            }
        }
        return obtainedLocations;
    }
}