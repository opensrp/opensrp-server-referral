package org.opensrp.referral.service;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;
import com.squareup.okhttp.*;
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
import java.util.stream.Collectors;

@Service
public class LocationService extends OpenmrsLocationService {

    private static Logger logger = LoggerFactory.getLogger(OpenmrsLocationService.class);

    public LocationService(String openmrsUrl, String user, String password) {
        super(openmrsUrl, user, password);
    }

    private String getURL(String url) {
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

    public List<Location> getHealthFacilityLocationsInCouncil(String uuid) throws JSONException {
        List<Location> allLocationsList = getAllLocations();
        logger.error(new Gson().toJson(allLocationsList));
        return getCouncilFacilities(uuid, allLocationsList);
    }

    public List<Location> getAllLocations() throws JSONException {
        String response = this.getURL(HttpUtil.removeEndingSlash(this.OPENMRS_BASE_URL) + "/" + "ws/rest/v1/location" + "?v=custom:(uuid,display,name,tags:(uuid,display),parentLocation:(uuid,display),attributes)");
        logger.info("response received : " + response);
        if (!StringUtils.isEmptyOrWhitespaceOnly(response) && (new JSONObject(response)).has("results")) {
            List<Location> allLocations = new ArrayList<>();
            JSONArray results = new JSONObject(response).getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                allLocations.add(makeLocation(results.getJSONObject(i).toString()));
            }
            return allLocations;

        }
        return null;
    }

    private Location makeLocation(String locationJson) throws JSONException {
        logger.info("makeLocation: " + locationJson);
        JSONObject obj = new JSONObject(locationJson);
        Location patentLocation = this.getParent(obj);
        Location location = new Location(obj.getString("uuid"), obj.getString("name"), null, null, patentLocation, null, null);
        JSONArray tags = obj.getJSONArray("tags");

        for (int i = 0; i < tags.length(); ++i) {
            location.addTag(tags.getJSONObject(i).getString("display"));
        }

        JSONArray attributes = obj.getJSONArray("attributes");

        for (int i = 0; i < attributes.length(); ++i) {
            boolean voided = attributes.getJSONObject(i).optBoolean("voided");
            if (!voided) {
                String ad = attributes.getJSONObject(i).getString("display");
                location.addAttribute(ad.substring(0, ad.indexOf(":")), ad.substring(ad.indexOf(":") + 2));
            }
        }
        return location;
    }

    public List<Location> getCouncilFacilities(String uuid, List<Location> allLocations) {

        List<Location> filteredList = allLocations.stream()
                .filter(location -> location.getLocationId().contains(uuid))
                .collect(Collectors.toList());
        Location location;

        //if the filtered list is empty return empty list
        if (filteredList.size() > 0) {
            location = filteredList.get(0);
        } else {
            return new ArrayList<>();
        }

        if (location.getTags().contains(AllowedTags.VILLAGE.toString()) || location.getTags().contains(AllowedTags.WARD.toString()) || location.getTags().contains(AllowedTags.FACILITY.toString())) {
            return getCouncilFacilities(location.getParentLocation().getLocationId(), allLocations);
        } else if (location.getTags().contains(AllowedTags.COUNCIL.toString())) {
            return getFacilitiesByCouncilId(location.getLocationId(), allLocations);
        } else {
            return null;
        }
    }


    private List<Location> getFacilitiesByCouncilId(String uuid, List<Location> allLocations) {
        List<Location> facilitiesLocations = new ArrayList<>();
        for (Location location : allLocations) {
            try {
                if (location.getParentLocation().getLocationId().equals(uuid) && location.getTags().contains(LocationService.AllowedTags.FACILITY.toString())) {
                    facilitiesLocations.add(location);
                }
            } catch (NullPointerException e) {
                logger.error(e.getMessage());
            }
        }

        return facilitiesLocations;
    }

    public enum AllowedTags {
        COUNTRY("Country"),
        ZONE("Zone"),
        REGION("Region"),
        COUNCIL("Council"),
        WARD("Ward"),
        VILLAGE("Village"),
        FACILITY("Facility");

        private final String display;

        AllowedTags(String display) {
            this.display = display;
        }

        public String toString() {
            return this.display;
        }
    }
}