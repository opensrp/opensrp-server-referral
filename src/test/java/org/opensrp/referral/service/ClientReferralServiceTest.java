package org.opensrp.referral.service;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
public class ClientReferralServiceTest {

    private static Logger logger = LoggerFactory.getLogger(ClientReferralService.class);
    private ClientReferralService clientReferralService;

    @Before
    public void setUp() {
        clientReferralService = new ClientReferralService();
    }

    @Test
    public void testReferralService() {
        try {
            Assert.assertTrue(clientReferralService.testReferralService().has("success"));
            Assert.assertEquals(clientReferralService.testReferralService().getString("success"), "testing referral api");
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }
}
