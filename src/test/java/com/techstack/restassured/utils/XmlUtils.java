package com.techstack.restassured.utils;

import io.restassured.path.xml.XmlPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XmlUtils {

    public static String getValueFromXmlResponse(String response, String path) {

        XmlPath xmlPath = new XmlPath(response);
        String value = xmlPath.getString(path);
        return value;
    }

    public static String generateStringFromXmlResource(String fileName) throws IOException {

        Path resourceDirectory = Paths.get("src","test", "resources", "xml");
        Path filePath = Paths.get(resourceDirectory.toString(), fileName);
        return new String(Files.readAllBytes(filePath));
    }
}
