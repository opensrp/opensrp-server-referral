package org.opensrp.referral.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Added as placeholder for client referral work
 */
@Service
public class ClientReferralService {
    public JSONObject testReferralService() throws JSONException {
        return new JSONObject().put("success", "testing referral api");
    }
}
