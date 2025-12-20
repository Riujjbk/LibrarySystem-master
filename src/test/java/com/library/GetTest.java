package com.library;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@Disabled
public class GetTest {
    @Test
    void get(){
        given()
                .param("name","11")
                .param("age","11")
        .when()
                .post("http://localhost:8080/get")
        .then()
                .statusCode(200);
    }

    @Test
    void post(){
        given()
                .param("name","11")
        .when()
                .post("http://localhost:8080/get")
        .then()
                .statusCode(200);
    }
}
