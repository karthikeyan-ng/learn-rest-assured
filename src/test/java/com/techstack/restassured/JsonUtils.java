package com.techstack.restassured;

import io.restassured.path.json.JsonPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtils {

    public static String generateStringFromJsonResource(String fileName) throws IOException {

        Path resourceDirectory = Paths.get("src","test", "resources", "json");
        Path filePath = Paths.get(resourceDirectory.toString(), fileName);
        return new String(Files.readAllBytes(filePath));
    }

    public static String generateStringFromXmlResource(String fileName) throws IOException {

        Path resourceDirectory = Paths.get("src","test", "resources", "xml");
        Path filePath = Paths.get(resourceDirectory.toString(), fileName);
        return new String(Files.readAllBytes(filePath));
    }

    public static JsonPath getJsonPathFromResponse(String response) {

        JsonPath jsonPath = new JsonPath(response);
        return jsonPath;
    }

    public static String getValueFromJsonResponse(String response, String path) {

        JsonPath jsonPath = new JsonPath(response);
        String value = jsonPath.getString(path);
        return value;
    }
}
