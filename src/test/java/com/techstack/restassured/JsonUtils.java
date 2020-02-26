package com.techstack.restassured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonUtils {

    public static String generateStringFromResource(String fileName) throws IOException {

        Path resourceDirectory = Paths.get("src","test", "resources", "json");
        Path filePath = Paths.get(resourceDirectory.toString(), fileName);
        return new String(Files.readAllBytes(filePath));
    }
}
