package com.techstack.restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static com.techstack.restassured.GoogleMapPlacesApiConsts.ADD_A_PLACE_JSON;
import static com.techstack.restassured.GoogleMapPlacesApiConsts.ADD_A_PLACE_XML;
import static com.techstack.restassured.GoogleMapPlacesApiConsts.DELETE_A_PLACE;
import static com.techstack.restassured.GoogleMapPlacesApiConsts.NEAR_BY_SEARCH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoogleMapPlacesApiTests {

    private Properties properties;

    @BeforeEach
    public void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("environment.properties"));
    }

    @DisplayName("GET: Find Location for the given radius")
    @Test
    void simplePlacesLookup_usingGet() {

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyCBsZ88adr5hIyelfJHyVbM2BBwR84TJYE

        RestAssured.baseURI = properties.getProperty("HOST_URI");

        Response response = given().
                log().all().
            param("location", "-33.8670522,151.1957362").
            param("radius", "500").
            param("key", properties.getProperty("PLACES_API_KEY")).
        when().
            get(NEAR_BY_SEARCH).
        then().
            assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON).
                extract().response();

        JsonPath jsonPath = JsonUtils.getJsonPathFromResponse(response.asString());
        assertEquals(Integer.valueOf(20), jsonPath.get("results.size()"));
    }

    /**
     * Add a location to Google Places. But, this end point is removed from Google Places API.
     * Hence, I used the modified version as shown below
     */
    @DisplayName("Add a location to Google Places.")
    @Test
    void simplePlacesLookup_usingJsonPost() throws Exception {

        RestAssured.baseURI = properties.getProperty("HOST_URI1");

        String content = JsonUtils.generateStringFromJsonResource("AddALocation_PayLoad.json");

        given().
            queryParam("key", properties.getProperty("PLACES_API_KEY1")).
            body(content).
        when().
            post(ADD_A_PLACE_JSON).
        then().
            assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON)
//                and().
//                body("status", equalTo("OK"))
        ;
    }

    /**
     * Add a location to Google Places. But, this end point is removed from Google Places API.
     * Hence, I used the modified version as shown below
     */
    @DisplayName("Add a location to Google Places.")
    @Test
    void simplePlacesLookup_usingXmlPost() throws Exception {

        RestAssured.baseURI = properties.getProperty("HOST_URI1");

        String content = JsonUtils.generateStringFromXmlResource("AddALocation_PayLoad.xml");

        Response response = given().
            queryParam("key", properties.getProperty("PLACES_API_KEY1")).
            body(content).
        when().
            post(ADD_A_PLACE_XML).
        then().
            assertThat().
                statusCode(200).
                    and().
                contentType(ContentType.XML).
                extract().
                response();

        System.out.println(response.asString());
        String res = response.asString();

        XmlPath xmlPath = new XmlPath(response.asString());
        assertEquals("OK", XmlUtils.getValueFromXmlResponse(res, "response.status"));
        assertEquals("APP", XmlUtils.getValueFromXmlResponse(res, "response.scope"));

    }

    @DisplayName("Add a location to Google Places and Delete the same place by using PlaceId")
    @Test
    void addAPlaceAndDeleteAddedPlace_usingPostAndDelete() throws Exception {

        String content = JsonUtils.generateStringFromJsonResource("AddALocation_PayLoad.json");

        RestAssured.baseURI = properties.getProperty("HOST_URI1");

        //1. Grab the response
        Response response = given().
                queryParam("key", properties.getProperty("PLACES_API_KEY1")).
                body(content).
                when().
                    post(ADD_A_PLACE_JSON).
                then().
                    assertThat().
                        statusCode(200).
                            and().
                        contentType(ContentType.JSON).
                            and().
                        body("status", equalTo("OK")).
                        extract().response();

        //2. Grab the PlaceId from the response
        String responseString = response.asString();
        System.out.println(responseString);

        JsonPath path = new JsonPath(responseString);
        String placeId = path.get("place_id");
        System.out.println(placeId);

        //3. Using placeId and Delete the place

        given().
            queryParam("key", properties.getProperty("PLACES_API_KEY1")).
        body("{\"place_id\": \"" + placeId + "\"}").
            when().
                post(DELETE_A_PLACE).
            then().
                assertThat().
                    statusCode(200).
                        and().
                    contentType(ContentType.JSON).
                        and().
                    body("status", equalTo("OK"));

    }

}
