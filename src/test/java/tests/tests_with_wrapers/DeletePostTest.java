package tests.tests_with_wrapers;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import util.APITestRunner;

import static util.APIAssert.assertThat;

public class DeletePostTest extends APITestRunner {

    @BeforeMethod
    public final void createPostMessage() {

        Response createPostResponse;

        createPostResponse = createPost("MY MESSAGE");

        assertThat(createPostResponse).isHTTPCode(200);

        // parsing JSON to get ID for deleting current message
        POST_MESSAGE_ID = createPostResponse.body().jsonPath().get("response.id");
    }

    @Test
    public final void testDeletePost() {

        assertThat(deletePostByID(POST_MESSAGE_ID)).isHTTPCode(200);
    }
}
