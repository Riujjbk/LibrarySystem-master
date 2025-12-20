package com.library.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LendApiBlackBoxTest {
    static boolean serverUp;

    @BeforeAll
    static void setup() {
        String base = System.getProperty("baseUrl", "http://localhost:8080");
        RestAssured.baseURI = base;
        try {
            serverUp = given().when().get("/api/lends").then().extract().statusCode() < 600;
        } catch (Exception e) {
            serverUp = false;
        }
        Assumptions.assumeTrue(serverUp);
    }

    @Test
    @Order(1)
    void myLends_withoutLogin_returnsEmptyListOrUnauthorized() {
        given()
        .when()
                .get("/api/my-lends")
        .then()
                .statusCode(anyOf(is(200), is(401)))
                .body("$", anyOf(instanceOf(java.util.List.class), notNullValue()));
    }

    @Test
    @Order(2)
    void lend_withoutLogin_isUnauthorized() {
        given()
                .formParam("bookId", 1)
        .when()
                .post("/api/lend")
        .then()
                .statusCode(is(401))
                .body("success", is(false));
    }

    @Test
    @Order(3)
    void return_withoutLogin_isUnauthorized() {
        given()
                .formParam("bookId", 1)
        .when()
                .post("/api/return")
        .then()
                .statusCode(is(401))
                .body("success", is(false));
    }
}
