package org.opensrp.referral.controller;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.referral.service.CommonLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/location/")
public class CommonLocationController {

    private CommonLocationService commonLocationService;

    @Autowired
    public CommonLocationController(CommonLocationService commonLocationService) {
        this.commonLocationService = commonLocationService;
    }


    /**
     * This method receives the uuid of a location within the location hierarchy level
     *  return a list of all other location within the location hierarchy level matching the requested tags
     * @param  jsonObject object containing
     *              locationUUID ,string of any location within the hierarchy level within the a hierarchy,
     *              locationTopLevel, string of the tag name of top location hierarchy level to query locations from,
     *              locationTagsQueried, a jsonArray of containing tags of locations to be returned
     * @return List of all facilities within the same hierarchy level.
     */

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "locations/getLocationsByTags")
    public ResponseEntity<String> getLocationsWithinALevelAndTags(@RequestBody JSONObject jsonObject) throws JSONException {
        return new ResponseEntity<>(new Gson().toJson(
                commonLocationService.getLocationsWithinAHierarchyLevel(
                        jsonObject.getString("locationUUID"),
                        jsonObject.getString("locationTopLevel"),
                        jsonObject.getJSONArray("locationTagsQueried")
                )
        ), HttpStatus.OK);
    }

}
