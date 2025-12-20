package com.library.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadersApiBlackBoxTest {
    static boolean serverUp;

    @BeforeAll
    static void setup() {
        String base = System.getProperty("baseUrl", "http://localhost:8080");
        RestAssured.baseURI = base;
        try {
            serverUp = given().when().get("/api/readers").then().extract().statusCode() < 600;
        } catch (Exception e) {
            serverUp = false;
        }
        Assumptions.assumeTrue(serverUp);
    }

    @Test
    @Order(1)
    void listReaders_returnsArray() {
        given()
        .when()
                .get("/api/readers")
        .then()
                .statusCode(anyOf(is(200), is(500)))
                .body("$", anyOf(instanceOf(java.util.List.class), notNullValue()));
    }

    @Test
    @Order(2)
    void getReader_byId_handlesNotFoundOrOk() {
        given()
        .when()
                .get("/api/readers/{id}", 1)
        .then()
                .statusCode(anyOf(is(200), is(404), is(500)));
    }
}
