package com.techstack.api.beans;

import lombok.Data;

import java.util.List;

@Data
public class Course {

    private List<WebAutomation> webAutomations;
    private List<Api> apis;
    private List<Mobile> mobile;

}
