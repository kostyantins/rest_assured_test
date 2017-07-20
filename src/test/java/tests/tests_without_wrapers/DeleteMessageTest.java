package tests.tests_without_wrapers;

import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import util.AbstractBaseTest;

import static io.restassured.RestAssured.given;

public class DeleteMessageTest extends AbstractBaseTest {

    private static String POST_MESSAGE_ID;

    @BeforeMethod
    public final void createPost() {

        final String POST_MESSAGE = "HAY ELEKS";

        Response response = createPost(POST_MESSAGE);

        response
                .then()
                .assertThat()
                .statusCode(200);

        // parsing JSON to get ID for deleting current message
        POST_MESSAGE_ID = response.body().jsonPath().get("response.id");
    }

    @Test
    public final void testDeleteMessage (){

        deletePostByID(POST_MESSAGE_ID)
                .then()
                .assertThat()
                .statusCode(200);
    }
}
