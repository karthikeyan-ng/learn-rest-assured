package com.techstack.restassured.googlemapsapi;

import com.sun.tools.javac.util.List;
import com.techstack.restassured.googlemapsapi.api.AddPlace;
import com.techstack.restassured.googlemapsapi.api.Location;
import com.techstack.restassured.utils.JsonUtils;
import com.techstack.restassured.utils.XmlUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static com.techstack.restassured.googlemapsapi.GoogleMapPlacesApiConsts.ADD_A_PLACE_JSON;
import static com.techstack.restassured.googlemapsapi.GoogleMapPlacesApiConsts.ADD_A_PLACE_XML;
import static com.techstack.restassured.googlemapsapi.GoogleMapPlacesApiConsts.DELETE_A_PLACE;
import static com.techstack.restassured.googlemapsapi.GoogleMapPlacesApiConsts.NEAR_BY_SEARCH;
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

        RestAssured.baseURI = properties.getProperty("googleMaps.hostUri");

        Response response = given().
                log().all().
            param("location", "-33.8670522,151.1957362").
            param("radius", "500").
            param("key", properties.getProperty("googleMaps.placesApiKey")).
        when().
            get(NEAR_BY_SEARCH).
        then().
            log().all().
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

        RestAssured.baseURI = properties.getProperty("googleMaps.hostUri1");

        String content = JsonUtils.generateStringFromJsonResource("AddALocation_PayLoad.json");
        AddPlace place = createPlace();

        given().
            queryParam("key", properties.getProperty("googleMaps.placesApiKey1")).
            //body(content).  //<== using JSON as string
            body(place).      //<== using JSON as a serialized Object
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

    @DisplayName("Add a location to Google Places using Spec Builder.")
    @Test
    void addAPlace_usingJsonPost() throws Exception {

        AddPlace place = createPlace();

        RequestSpecification reqSepc = new RequestSpecBuilder()
                .setBaseUri(properties.getProperty("googleMaps.hostUri1"))
                .addQueryParam("key", properties.getProperty("googleMaps.placesApiKey1"))
                .build();

        ResponseSpecification resSpec = new ResponseSpecBuilder()
                .expectStatusCode(200).expectContentType(ContentType.JSON).build();

        // Simplified logic
        given().spec(reqSepc).body(place).
        when().post(ADD_A_PLACE_JSON).
        then().spec(resSpec);
    }

    private AddPlace createPlace() {
        AddPlace addPlace = new AddPlace();
        addPlace.setAccuracy(50);
        addPlace.setAddress("48 Pirrama Road, Pyrmont, NSW 2009, Australia");
        addPlace.setLanguage("en-AU");
        addPlace.setName("Google Shoes!");
        addPlace.setPhone_number("(02) 9374 4000");
        addPlace.setWebsite("http://www.google.com.au/");
        addPlace.setTypes(List.of("shoe_store"));
        Location location = new Location();
        location.setLat(-33.866971);
        location.setLng(151.195875);
        addPlace.setLocation(location);
        return addPlace;
    }

    /**
     * Add a location to Google Places. But, this end point is removed from Google Places API.
     * Hence, I used the modified version as shown below
     */
    @DisplayName("Add a location to Google Places.")
    @Test
    void simplePlacesLookup_usingXmlPost() throws Exception {

        RestAssured.baseURI = properties.getProperty("googleMaps.hostUri1");

        String content = XmlUtils.generateStringFromXmlResource("AddALocation_PayLoad.xml");

        Response response = given().
            queryParam("key", properties.getProperty("googleMaps.placesApiKey1")).
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
        Assertions.assertEquals("OK", XmlUtils.getValueFromXmlResponse(res, "response.status"));
        assertEquals("APP", XmlUtils.getValueFromXmlResponse(res, "response.scope"));

    }

    @DisplayName("Add a location to Google Places and Delete the same place by using PlaceId")
    @Test
    void addAPlaceAndDeleteAddedPlace_usingPostAndDelete() throws Exception {

        String content = JsonUtils.generateStringFromJsonResource("AddALocation_PayLoad.json");

        RestAssured.baseURI = properties.getProperty("googleMaps.hostUri1");

        //1. Grab the response
        Response response = given().
                queryParam("key", properties.getProperty("googleMaps.placesApiKey1")).
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
            queryParam("key", properties.getProperty("googleMaps.placesApiKey1")).
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
