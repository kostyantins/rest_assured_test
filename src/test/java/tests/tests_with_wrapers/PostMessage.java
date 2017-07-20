package tests.tests_with_wrapers;

import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import util.APITestRunner;

import static util.APIAssert.assertThat;

public class PostMessage extends APITestRunner {

    @Test
    public final void testPostMessage() {

        Response createPostResponse;

        createPostResponse = createPost("MY MESSAGE");

        assertThat(createPostResponse).isHTTPCode(200);

        // parsing JSON to get ID for deleting current message
        POST_MESSAGE_ID = createPostResponse.body().jsonPath().get("response.id");
    }

    @AfterMethod
    public final void deletePostedMessage() {

        assertThat(deletePostByID(POST_MESSAGE_ID)).isHTTPCode(200);
    }
}
