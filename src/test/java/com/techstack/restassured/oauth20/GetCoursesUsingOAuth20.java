package com.techstack.restassured.oauth20;

import com.techstack.api.beans.GetCourse;
import com.techstack.restassured.utils.JsonUtils;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
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
    void getCourses() throws Exception{

        RestAssured.baseURI = properties.getProperty("library.hostUri");

        /**
         * If you are using Authorization Code type you have to use Step 1,2,3
         * If you are using Client Credentials type you have to use Step 2,3
         */

        /** STEP-1: Get the Authorization Code from Provider  */
        // Open the Google Login Authorization to validate the Username and password to get the access token code
//        System.setProperty("webdriver.chrome.driver", "/Users/rabodevops17/Documents/Downloaded Software/chromedriver");
//        WebDriver driver = new ChromeDriver();
//        driver.get("https://accounts.google.com/signin/oauth/identifier?client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&scope=https://www.googleapis.com/auth/userinfo.email&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php");
//        driver.findElement(By.cssSelector("input[type='email']")).sendKeys("<email-id>");
//        driver.findElement(By.cssSelector("input[type='email']")).sendKeys(Keys.ENTER);
//        Thread.sleep(3000);
//        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("<email-password>");
//        driver.findElement(By.cssSelector("input[type='password']")).sendKeys(Keys.ENTER);
//        Thread.sleep(4000);
//        String url = driver.getCurrentUrl();
        String url = "https://rahulshettyacademy.com/getCourse.php?code=4%2FxAHPwBBx4TrBTMo0zo4hwKNBI8eSxDP11VsU1ac1jDH4W59LLLXzm34hj1Df7yYUr2-P8wnqSeD4KvsKRskvkno&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0&prompt=consent#";
        System.out.println(url);

        String partialCode = url.split("code")[1];
        String code = partialCode.split("&scope")[0];
        System.out.println(code);

        /** STEP-2: Use the Authorization Code and Call Token service to get AccessToken */

        // Call Authorization Service to get the access_token
        String accessTokenResponse = given().
            urlEncodingEnabled(false).
            queryParams("code", code).
            queryParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com").
            queryParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W").
            queryParams("grant_type", "authorization_code").
            queryParams("state", "verifyfjdss").
            queryParams("session_state", "ff4a89d1f7011eb34eef8cf02ce4353316d9744b..7eb8").
            queryParams("redirect_uri", "https://rahulshettyacademy.com/getCourses.php").
        when().
            //log().all().
            post("https://googleapis.com/oauth2/v4/token").
            asString();

        System.out.println(accessTokenResponse);

        JsonPath path = JsonUtils.getJsonPathFromResponse(accessTokenResponse);
        String accessToken = path.getString("access_token");

        /** STEP-3: Call the Actual Server to get the response by using Access Token */
        // Use the Access Token
        GetCourse course = given().
            queryParam("access_token", accessToken).
            expect().defaultParser(Parser.JSON).
        when().
            get(GET_COURSES).as(GetCourse.class);

        System.out.println(course);
        System.out.println(course.getInstructor());
    }

}
