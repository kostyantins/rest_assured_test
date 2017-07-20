package tests.tests_without_wrapers;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetDiscusTest {

    @Test
    public void testGetDiscus() {

        given()
                .when()
                .get("https://disqus.com/api")
                .then()
                .statusCode(200);
    }
}
