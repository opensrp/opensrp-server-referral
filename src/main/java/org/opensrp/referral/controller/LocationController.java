package org.opensrp.referral.controller;

import com.google.gson.Gson;
import org.json.JSONException;
import org.opensrp.referral.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/location/")
public class LocationController {

    private LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }


    @RequestMapping("district-facilities/{uuid}")
    @ResponseBody
    public ResponseEntity<String> getLocationTree(@PathVariable("uuid") final String uuid) throws JSONException {
        return new ResponseEntity<>(new Gson().toJson(locationService.getHealthFacilityLocationsInCouncil(uuid)), HttpStatus.OK);
    }


}
