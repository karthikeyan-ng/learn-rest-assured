package com.techstack.api.beans;

import lombok.Data;

import java.util.List;

@Data
public class GetCourse {

    private String url;
    private String services;
    private String expertise;
    private List<Course> courses;
    private String instructor;
    private String linkedIn;
}
