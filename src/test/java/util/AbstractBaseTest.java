package util;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.log4testng.Logger;

import static io.restassured.RestAssured.given;
import static util.BaseAPIData.*;

public abstract class AbstractBaseTest {

    static private final Logger LOGGER = Logger.getLogger(AbstractBaseTest.class);

    public static String POST_MESSAGE_ID;
    public static String REPLY_MESSAGE_ID;

    public final Response deletePostByID(final String messageId) {

        return given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&post=" + messageId + "&access_token=" + ACCESS_TOKEN)
                .basePath(FOR_DELETE)
                .when()
                .post();
    }

    public final Response createPost(final String postMessage) {

        return given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&thread=" + THREAD + "&message=" + postMessage + "&access_token=" + ACCESS_TOKEN)
                .basePath(FOR_CREATE)
                .when()
                .post();
    }

    public final Response editPost(final String messageToEdit) {

        return given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&api_secret=" + API_SECRET + "&message=" + messageToEdit + "&access_token=" + ACCESS_TOKEN + "&post=" + POST_MESSAGE_ID)
                .basePath(FOR_EDIT)
                .when()
                .post();
    }

    public final Response replyPost(final String messageToReply) {

        return given()
                .accept(ContentType.JSON)
                .body("api_key=" + API_KEY + "&thread=" + THREAD + "&message=" + messageToReply + "&access_token=" + ACCESS_TOKEN + "&parent=" + POST_MESSAGE_ID)
                .basePath(FOR_CREATE)
                .when()
                .post();
    }

    public final Response getResource(final String URL) {

        return given()
                .when()
                .get(URL);
    }

    public final String getJSONValue(final Response response, final String JSONPath) {

        return response.body().jsonPath().get(JSONPath);
    }
}


