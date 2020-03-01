package com.techstack.restassured.oauth20;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static com.techstack.restassured.oauth20.GetCoursesConsts.GET_COURSES;
import static io.restassured.RestAssured.given;

public class GetCoursesUsingOAuth20 {

    private Properties properties;

    @BeforeEach
    public void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("environment.properties"));
    }

    @DisplayName("Get a List of Courses using OAuth 2.0")
    @Test
    void getCourses() {

        RestAssured.baseURI = properties.getProperty("library.hostUri");

        String response = given().
            log().all().
            queryParam("access_token", "").
        when().
            get(GET_COURSES).asString();

        System.out.println(response);
    }

}
