package com.techstack.api.beans;

import lombok.Data;

@Data
public class GetCourse {

    private String url;
    private String services;
    private String expertise;
    private Course courses;
    private String instructor;
    private String linkedIn;
}
