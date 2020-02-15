package com.techstack.restassured;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


public class GoogleMapPlacesApiTests {

    @DisplayName("Find Location for the given radius")
    @Test
    void simplePlacesLookup() {

        RestAssured.baseURI = "https://maps.googleapis.com";

        given().
            param("location", "-33.8670522,151.1957362").
            param("radius", "500").
            param("Key", "AIzaSyCBsZ88adr5hIyelfJHyVbM2BBwR84TJYE").
        when().
            get("/maps/api/place/nearbysearch/json").
        then().
            assertThat().
                statusCode(200);


    }
}
