package org.opensrp.referral.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.opensrp.api.domain.Location;

import java.util.List;

@RunWith(JUnit4.class)
public class LocationServiceTest {
    public LocationService locationService;

    @Before
    public void setUp() throws Exception {
        locationService = new LocationService("https://demo.openmrs.org/openmrs","admin","Admin123");
    }

    @Test
    public void getCouncilFacilities() {

        String locationJson = "[{\"locationId\":\"457c129d-06fc-4e2c-9394-d7ea85b2249f\",\"name\":\"Dar es Salaam\",\"parentLocation\":{\"locationId\":\"b7b70074-b8f1-4901-9238-1908b5d1f7d4\",\"name\":\"Eastern Zone\",\"voided\":false},\"tags\":[\"Region\"],\"voided\":false},{\"locationId\":\"b7b70074-b8f1-4901-9238-1908b5d1f7d4\",\"name\":\"Eastern Zone\",\"parentLocation\":{\"locationId\":\"82781f37-5bfd-45f3-8f1b-dad0a55b0570\",\"name\":\"Tanzania\",\"voided\":false},\"tags\":[\"Zone\"],\"voided\":false},{\"locationId\":\"8f61076c-bcde-4dac-af52-5feeece5c48a\",\"name\":\"Ebrahim Haji\",\"parentLocation\":{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"voided\":false},\"tags\":[\"Facility\"],\"voided\":false},{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"parentLocation\":{\"locationId\":\"457c129d-06fc-4e2c-9394-d7ea85b2249f\",\"name\":\"Dar es Salaam\",\"voided\":false},\"tags\":[\"Council\"],\"voided\":false},{\"locationId\":\"25820e25-76c5-455a-812d-0934db2564f5\",\"name\":\"Madona\",\"parentLocation\":{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"voided\":false},\"tags\":[\"Facility\"],\"voided\":false},{\"locationId\":\"846955fd-02cd-4fb4-bfcd-712dc2f8c922\",\"name\":\"Mchafukoge\",\"parentLocation\":{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"voided\":false},\"tags\":[\"Ward\"],\"voided\":false},{\"locationId\":\"2b17d7a9-3305-4835-8ca4-9de36eabad19\",\"name\":\"Mnazi Mmoja\",\"parentLocation\":{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"voided\":false},\"tags\":[\"Facility\"],\"voided\":false},{\"locationId\":\"1ea3645a-90b0-4ec4-8b43-f7d85ea9e1a3\",\"name\":\"Tabata\",\"parentLocation\":{\"locationId\":\"d8fca020-dcac-47e7-b9fe-9d728588294b\",\"name\":\"Ilala MC\",\"voided\":false},\"tags\":[\"Ward\"],\"voided\":false},{\"locationId\":\"c760f2de-1c92-4495-8003-868dd25fe410\",\"name\":\"Tabata Dampo\",\"parentLocation\":{\"locationId\":\"1ea3645a-90b0-4ec4-8b43-f7d85ea9e1a3\",\"name\":\"Tabata\",\"voided\":false},\"tags\":[\"Village\"],\"voided\":false},{\"locationId\":\"82781f37-5bfd-45f3-8f1b-dad0a55b0570\",\"name\":\"Tanzania\",\"tags\":[\"Country\"],\"voided\":false},{\"locationId\":\"c37c218a-2cad-449a-8c7b-352dffdb3a39\",\"name\":\"Uhuru\",\"parentLocation\":{\"locationId\":\"846955fd-02cd-4fb4-bfcd-712dc2f8c922\",\"name\":\"Mchafukoge\",\"voided\":false},\"tags\":[\"Village\"],\"voided\":false},{\"locationId\":\"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\",\"name\":\"Unknown Location\",\"voided\":false},{\"locationId\":\"fa92a452-bc41-427c-b382-9ebdfc18e1b7\",\"name\":\"Zanaki\",\"parentLocation\":{\"locationId\":\"846955fd-02cd-4fb4-bfcd-712dc2f8c922\",\"name\":\"Mchafukoge\",\"voided\":false},\"tags\":[\"Village\"],\"voided\":false}]\n";
        List<Location> allLocations = new Gson().fromJson(locationJson, new TypeToken<List<Location>>() {
        }.getType());
        List<Location> councilFacilities = locationService.getCouncilFacilities("25820e25-76c5-455a-812d-0934db2564f5", allLocations);

        Assert.assertEquals(3, councilFacilities.size());
        Assert.assertEquals("Ebrahim Haji", councilFacilities.get(0).getName());
        Assert.assertEquals("Madona", councilFacilities.get(1).getName());
        Assert.assertEquals("Mnazi Mmoja", councilFacilities.get(2).getName());

    }

    @Test
    public void getAllLocations() {
        try {
            Assert.assertTrue(locationService.getAllLocations().size() > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}