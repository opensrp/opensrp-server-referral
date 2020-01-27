package org.opensrp.referral.controller;

import com.google.gson.Gson;
import org.json.JSONException;
import org.opensrp.referral.service.ReferralLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/location/")
public class ReferralLocationController {

    private ReferralLocationService referralLocationService;

    @Autowired
    public ReferralLocationController(ReferralLocationService referralLocationService) {
        this.referralLocationService = referralLocationService;
    }


    /**
     * This method receives the uuid of a facility location that team member (chw) is tied up to
     * or a uuid of a village that team member is serving and return a list of all other facilities within the district/council of the uuid
     * @param  uuid uuid of a facility or a village.
     * @return List of all facilities within the same district/council.
     */

    @RequestMapping("facilities/{uuid}")
    @ResponseBody
    public ResponseEntity<String> getFacilitiesWithinACouncil(@PathVariable("uuid") final String uuid) throws JSONException {
        return new ResponseEntity<>(new Gson().toJson(referralLocationService.getLocationsWithinAHierarchyLevel(uuid)), HttpStatus.OK);
    }

}
