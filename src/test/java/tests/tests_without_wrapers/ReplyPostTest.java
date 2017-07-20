package tests.tests_without_wrapers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;

import static io.restassured.RestAssured.given;
import static util.BaseAPIData.*;

public class ReplyPostTest {

    private static String POST_MESSAGE_ID;
    private static String REPLY_MESSAGE_ID;

    @BeforeMethod
    public final void createPost() {

        final String POST_MESSAGE = "HAY ELEKS";

        RestAssured.baseURI = "https://disqus.com";

        Response response =
                given()
                        .accept(ContentType.JSON)
                        .body("api_key=" + API_KEY + "&thread=" + THREAD + "&message=" + POST_MESSAGE + "&access_token=" + ACCESS_TOKEN)
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

    @Test
    public final void testReplyPost() {

        final String POST_REPLY_MESSAGE = "HAY! AND WHAT??";

        RestAssured.baseURI = "https://disqus.com";

        Response replyPost = given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&thread=" + THREAD + "&message=" + POST_REPLY_MESSAGE + "&access_token=" + ACCESS_TOKEN + "&parent=" + POST_MESSAGE_ID)
                .basePath(FOR_CREATE)
                .when()
                .post();

        replyPost
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .log().all();

        REPLY_MESSAGE_ID = replyPost.body().jsonPath().get("response.id");
    }

    @AfterMethod
    public final void deletePost() throws MalformedURLException {

        RestAssured.baseURI = "https://disqus.com";

        given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&post=" + REPLY_MESSAGE_ID + "&access_token=" + ACCESS_TOKEN)
                .basePath(FOR_DELETE)
                .when()
                .post()
                .then()
                .statusCode(200)
                .log().all();

        given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&post=" + POST_MESSAGE_ID + "&access_token=" + ACCESS_TOKEN)
                .basePath(FOR_DELETE)
                .when()
                .post()
                .then()
                .statusCode(200)
                .log().all();
    }
}
