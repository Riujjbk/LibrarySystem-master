package com.library.api;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibraryApiScenarioTest {
    static boolean serverUp;
    static String baseUrl;
    static String username;
    static String password;
    static long readerId;
    static long bookId;
    static final SessionFilter readerSession = new SessionFilter();

    @BeforeAll
    static void setup() {
        baseUrl = System.getProperty("baseUrl", "http://localhost:8080");
        RestAssured.baseURI = baseUrl;
        try {
            serverUp = given().when().get("/api/books").then().extract().statusCode() < 600;
        } catch (Exception e) {
            serverUp = false;
        }
        Assumptions.assumeTrue(serverUp);
        username = "user_" + System.currentTimeMillis();
        password = "p" + (100000 + ThreadLocalRandom.current().nextInt(900000));
    }

    @Test
    @Order(1)
    void createReader_cardAndInfoAreCreated() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        payload.put("name", "测试读者");
        payload.put("sex", "男");
        payload.put("birth", "2000-01-01");
        payload.put("address", "测试地址");
        payload.put("phone", "13800000000");
        String body = given()
                .contentType("application/json")
                .body(payload)
        .when()
                .post("/api/readers")
        .then()
                .statusCode(200)
                .body("success", is(true))
                .body("readerId", notNullValue())
                .extract().asString();
        JsonPath jp = JsonPath.from(body);
        readerId = jp.getLong("readerId");
        given()
        .when()
                .get("/api/readers/{id}", readerId)
        .then()
                .statusCode(200)
                .body("readerId", is((int)readerId))
                .body("name", is("测试读者"))
                .body("phone", is("13800000000"));
    }

    @Test
    @Order(2)
    void loginAsReader_obtainSession() {
        Map<String, Object> login = new HashMap<>();
        login.put("username", username);
        login.put("passwd", password);
        given()
                .log().all()
                .contentType("application/json")
                .body(login)
                .filter(readerSession)
        .when()
                .post("/api/loginCheck")
        .then()
                .log().all()
                .statusCode(200)
                .body("stateCode", is("2"))
                .body("msg", containsString("登陆成功"));
    }

    @Test
    @Order(3)
    void createBook_thenFetchAndUpdate() {
        String name = "黑盒测试图书-" + System.currentTimeMillis();
        Map<String, Object> book = new HashMap<>();
        book.put("name", name);
        book.put("author", "测试作者");
        book.put("publish", "测试出版社");
        book.put("isbn", "9780000000001");
        book.put("price", 19.9);
        book.put("number", 3);
        String res = given()
                .contentType("application/json")
                .body(book)
        .when()
                .post("/api/books")
        .then()
                .statusCode(200)
                .body("success", is(true))
                .extract().asString();
        // 通过分页列表查回刚创建的图书ID
        String list = given()
                .queryParam("page", 1)
                .queryParam("size", 50)
                .queryParam("q", name)
        .when()
                .get("/api/books")
        .then()
                .statusCode(200)
                .body("records", notNullValue())
                .extract().asString();
        JsonPath lp = JsonPath.from(list);
        List<Map<String, Object>> records = lp.getList("records");
        Assertions.assertFalse(records.isEmpty(), "新建图书未在列表中出现");
        Object bidObj = records.get(0).getOrDefault("bookId", records.get(0).get("book_id"));
        bookId = Long.parseLong(String.valueOf(bidObj));
        given()
        .when()
                .get("/api/books/{id}", bookId)
        .then()
                .statusCode(200)
                .body("name", is(name))
                .body("author", is("测试作者"))
                .body("number", is(3));
        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("author", "测试作者");
        update.put("publish", "测试出版社");
        update.put("isbn", "9780000000001");
        update.put("price", 29.9);
        update.put("number", 2);
        given()
                .contentType("application/json")
                .body(update)
        .when()
                .put("/api/books/{id}", bookId)
        .then()
                .statusCode(200)
                .body("success", is(true));
        given()
        .when()
                .get("/api/books/{id}", bookId)
        .then()
                .statusCode(200)
                .body("price", either(is(29.9F)).or(is(29.9)))
                .body("number", is(2));
    }

    @Test
    @Order(4)
    void lendAndReturn_asReaderWithSession() {
        given()
                .filter(readerSession)
                .formParam("bookId", bookId)
        .when()
                .post("/api/lend")
        .then()
                .statusCode(200)
                .body("success", is(true));
        String myLends = given()
                .filter(readerSession)
        .when()
                .get("/api/my-lends")
        .then()
                .statusCode(200)
                .extract().asString();
        JsonPath jp = JsonPath.from(myLends);
        List<Map<String, Object>> lends = jp.getList("$");
        boolean foundActive = lends.stream().anyMatch(l ->
                String.valueOf(l.getOrDefault("bookId", l.get("book_id"))).equals(String.valueOf(bookId))
                        && (l.get("backDate") == null && l.get("back_date") == null));
        Assertions.assertTrue(foundActive, "借阅记录未出现或已归还");
        given()
                .filter(readerSession)
                .formParam("bookId", bookId)
        .when()
                .post("/api/return")
        .then()
                .statusCode(200)
                .body("success", is(true));
        String myLends2 = given()
                .filter(readerSession)
        .when()
                .get("/api/my-lends")
        .then()
                .statusCode(200)
                .extract().asString();
        JsonPath jp2 = JsonPath.from(myLends2);
        List<Map<String, Object>> lends2 = jp2.getList("$");
        boolean foundReturned = lends2.stream().anyMatch(l ->
                String.valueOf(l.getOrDefault("bookId", l.get("book_id"))).equals(String.valueOf(bookId))
                        && (l.get("backDate") != null || l.get("back_date") != null));
        Assertions.assertTrue(foundReturned, "归还记录未出现");
    }

    @Test
    @Order(5)
    void cleanup_deleteBookAndReader() {
        given()
        .when()
                .delete("/api/books/{id}", bookId)
        .then()
                .statusCode(200)
                .body("success", is(true));
        given()
        .when()
                .delete("/api/readers/{id}", readerId)
        .then()
                .statusCode(200)
                .body("success", is(true));
    }
}
