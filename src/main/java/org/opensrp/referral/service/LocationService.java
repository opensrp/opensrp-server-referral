package org.opensrp.referral.service;

import com.mysql.jdbc.StringUtils;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Address;
import org.opensrp.api.domain.Location;
import org.opensrp.common.util.HttpUtil;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LocationService extends OpenmrsLocationService {

    private static Logger logger = LoggerFactory.getLogger(OpenmrsLocationService.class);

    public LocationService() {
    }

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
        } catch (IOException var7) {
            logger.error(var7.getMessage(), var7);
        }

        return null;
    }

    public List<Location> getAllLocation() throws JSONException {
        String response = this.getURL(HttpUtil.removeEndingSlash(this.OPENMRS_BASE_URL) + "/" + "ws/rest/v1/location" + "?v=default");
        if (!StringUtils.isEmptyOrWhitespaceOnly(response) && (new JSONObject(response)).has("uuid")) {
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
        Location p = this.getParent(obj);
        Location l = new Location(obj.getString("uuid"), obj.getString("name"), (Address) null, (Map) null, p, (Set) null, (Map) null);
        JSONArray t = obj.getJSONArray("tags");

        for (int i = 0; i < t.length(); ++i) {
            l.addTag(t.getJSONObject(i).getString("display"));
        }

        JSONArray a = obj.getJSONArray("attributes");

        for (int i = 0; i < a.length(); ++i) {
            boolean voided = a.getJSONObject(i).optBoolean("voided");
            if (!voided) {
                String ad = a.getJSONObject(i).getString("display");
                l.addAttribute(ad.substring(0, ad.indexOf(":")), ad.substring(ad.indexOf(":") + 2));
            }
        }
        logger.info("location: " + ReflectionToStringBuilder.toString(l));
        return l;
    }

}