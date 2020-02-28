package com.techstack.restassured.libraryapi;

import com.techstack.restassured.utils.JsonUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Properties;

import static com.techstack.restassured.libraryapi.LibraryApisConsts.ADD_BOOK;
import static io.restassured.RestAssured.given;

public class LibraryApiTests {

    private Properties properties;

    @BeforeEach
    public void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("environment.properties"));
    }

    @DisplayName("Add a Book to Library.")
    @ParameterizedTest(name = "Added a book isbn {0} with aisle {1}")
    @CsvSource({"xyz, 125", "ghi, 500"})
    void addABook(String isbn, String aisleNumber) throws Exception {

        System.out.println("Processing for : " + isbn + " and " + aisleNumber);

        RestAssured.baseURI = properties.getProperty("library.hostUri");

        String content = JsonUtils.generateStringFromJsonResource("AddABook_PayLoad.json");

        content = content.
                    replaceAll("#isbn", isbn).
                    replaceAll("#aisleNumber", aisleNumber);

        Response response = given().
            log().all().
            header("Content-Type", ContentType.JSON).
            body(content).
        when().
            post(ADD_BOOK).
        then().
            assertThat().
                statusCode(200).
                extract().response();

        JsonPath path = JsonUtils.getJsonPathFromResponse(response.asString());
        String id = JsonUtils.getValueFromJsonResponse(path, "ID");
        Assertions.assertEquals(isbn + aisleNumber, id);
    }

}
