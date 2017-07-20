package tests.tests_without_wrapers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

import static io.restassured.RestAssured.given;
import static util.BaseAPIData.*;

public class PostMessageTest {

    private static String POST_MESSAGE_ID;

    @Test
    public void testPostDiscusS() throws MalformedURLException {

        final String POST_MESSAGE = "HAY ELEKS";

        RestAssured.baseURI = "https://disqus.com";

        Response response =
                given()
                        .accept(ContentType.JSON)
                        .body("api_key=" + API_KEY + "&thread=" + THREAD + "&message=" + POST_MESSAGE + "&access_token=" + ACCESS_TOKEN + "")
                        .basePath(FOR_CREATE)
                        .when()
                        .post();

        response
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .log().all();

        // parsing JSON to get ID for deleting current message
        POST_MESSAGE_ID = response.body().jsonPath().get("response.id");
    }

    @AfterMethod
    public final void deletePost() throws MalformedURLException {

        RestAssured.baseURI = "https://disqus.com";

        given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&post=" + POST_MESSAGE_ID + "&access_token=" + ACCESS_TOKEN + "")
                .basePath(FOR_DELETE)
                .when()
                .post()
                .then()
                .statusCode(200)
                .log().all();
    }
}
