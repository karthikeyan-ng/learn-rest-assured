package com.techstack.restassured;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class GoogleMapPlacesApiTests {

    @DisplayName("GET: Find Location for the given radius")
    @Test
    void simplePlacesLookup_usingGet() {

        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyCBsZ88adr5hIyelfJHyVbM2BBwR84TJYE

        RestAssured.baseURI = "https://maps.googleapis.com";

        given().
            param("location", "-33.8670522,151.1957362").
            param("radius", "500").
            param("key", "AIzaSyCBsZ88adr5hIyelfJHyVbM2BBwR84TJYE").
        when().
            get("/maps/api/place/nearbysearch/json").
        then().
            assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON).
                and().
//                body("results[0].geometry.location.lat", equalTo(-33.8688197d))
                body("results[0].name", equalTo("Sydney")).
                and().
                body("results[0].place_id", equalTo("ChIJP3Sa8ziYEmsRUKgyFmh9AQM")).
                and().
                header("Server", equalTo("scaffolding on HTTPServer2"))
        ;
    }

    /**
     * Add a location to Google Places. But, this end point is removed from Google Places API.
     * Hence, I used the modified version as shown below
     */
    @DisplayName("Add a location to Google Places.")
    @Test
    void simplePlacesLookup_usingPost() {

        RestAssured.baseURI = "http://216.10.245.166";

        given().
            queryParam("key", "qaclick123").
            body("{"+
                    "\"location\": {"+
                    "\"lat\": -33.8669710,"+
                    "\"lng\": 151.1958750"+
                    "},"+
                    "\"accuracy\": 50,"+
                    "\"name\": \"Google Shoes!\","+
                    "\"phone_number\": \"(02) 9374 4000\","+
                    "\"address\": \"48 Pirrama Road, Pyrmont, NSW 2009, Australia\","+
                    "\"types\": [\"shoe_store\"],"+
                    "\"website\": \"http://www.google.com.au/\","+
                    "\"language\": \"en-AU\""+
                    "}").
        when().
            post("/maps/api/place/add/json").
        then().
            assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON).
                and().
                body("status", equalTo("OK"))
        ;
    }


}
