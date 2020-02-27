package com.techstack.restassured;

import io.restassured.path.xml.XmlPath;

public class XmlUtils {

    public static String getValueFromXmlResponse(String response, String path) {

        XmlPath xmlPath = new XmlPath(response);
        String value = xmlPath.getString(path);
        return value;
    }
}
