package org.opensrp.referral.service;

import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LocationServiceTest {

    private static Logger logger = LoggerFactory.getLogger(OpenmrsLocationService.class);

    public LocationService locationService;
    @Before
    public void setUp() throws Exception {
        locationService = new LocationService("https://openmrs.ba-unified-stage.smartregister.org/","admin","fmujqPN4x7FnSc7t");
    }

    @Test
    public void getAllLocation() {
        try {
            logger.info(new Gson().toJson(locationService.getAllLocation()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}