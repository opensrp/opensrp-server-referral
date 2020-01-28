package org.opensrp.referral.controller;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.referral.service.ReferralLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
     * @param  jsonObject object containing
     *              locationUUID ,string of any location within the hierarchy level within the a hierarchy,
     *              locationTopLevel, string of the tag name of top location hierarchy,
     *              allowedTags, a jsonArray containing TAGS in the location hierarchy,
     *              locationTagsQueried, a jsonArray of containing tags of locations to be returned
     * @return List of all facilities within the same hierarchy level.
     */

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "locations/getLocationsByTags")
    public ResponseEntity<String> getFacilitiesWithinACouncil(@RequestBody JSONObject jsonObject) throws JSONException {
        return new ResponseEntity<>(new Gson().toJson(
                referralLocationService.getLocationsWithinAHierarchyLevel(
                        jsonObject.getString("locationUUID"),
                        jsonObject.getString("locationTopLevel"),
                        jsonObject.getJSONArray("allowedTags"),
                        jsonObject.getJSONArray("locationTagsQueried")
                )
        ), HttpStatus.OK);
    }

}
