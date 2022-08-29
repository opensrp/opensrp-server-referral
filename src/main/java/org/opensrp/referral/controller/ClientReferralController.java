package org.opensrp.referral.controller;

import org.json.JSONException;
import org.opensrp.referral.service.ClientReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/referral/")
public class ClientReferralController {

    private ClientReferralService clientReferralService;

    @Autowired
    public ClientReferralController(ClientReferralService clientReferralService) {
        this.clientReferralService = clientReferralService;
    }


    /**
     * Sample test to use as placeholder. Will delete this with actual implementations
     *
     * @return Stringified JSON response
     * @throws JSONException Exception thrown
     */
    @RequestMapping(method = GET, value = "referral/test")
    public ResponseEntity<String> getLocationsWithinALevelAndTags() throws JSONException {
        return new ResponseEntity<>(clientReferralService.testReferralService().toString(), HttpStatus.OK);
    }

}
