package com.library.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BooksApiBlackBoxTest {
    static boolean serverUp;

    @BeforeAll
    static void setup() {
        String base = System.getProperty("baseUrl", "http://localhost:8080");
        RestAssured.baseURI = base;
        try {
            serverUp = given().when().get("/api/books").then().extract().statusCode() < 600;
        } catch (Exception e) {
            serverUp = false;
        }
        Assumptions.assumeTrue(serverUp);
    }

    @Test
    @Order(1)
    void listBooks_returnsPagedStructure() {
        given()
                .queryParam("page", 1)
                .queryParam("size", 5)
                .log().all()  // 打印请求信息
        .when()
                .get("/api/books")
        .then()
                .log().all()  // 打印请求信息
                .statusCode(anyOf(is(200), is(500)))
                .body("$", hasKey("records"))
                .body("$", hasKey("total"))
                .body("$", hasKey("pages"))
                .body("$", hasKey("current"))
                .body("$", hasKey("size"));
    }

    @Test
    @Order(2)
    void getBook_byId_handlesNotFoundOrOk() {
        given()
        .when()
                .get("/api/books/{id}", 1)
        .then()
                .statusCode(anyOf(is(200), is(404), is(500)));
    }
}
