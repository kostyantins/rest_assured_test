package tests.tests_with_wrapers;

import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import util.APITestRunner;

import static util.APIAssert.assertThat;

public class ReplyPostTest extends APITestRunner {

    @BeforeMethod
    public final void createPostMessage() {

        Response createPostResponse;

        createPostResponse = createPost("MY TEST");

        assertThat(createPostResponse).isHTTPCode(200);

        // parsing JSON to get ID for deleting current message
        POST_MESSAGE_ID = createPostResponse.body().jsonPath().get("response.id");
    }

    @Test
    public final void testReplyPost() {

        Response replyPostResponse;

        replyPostResponse = replyPost("MY REPLY");

        assertThat(replyPostResponse).isHTTPCode(200);

        // parsing JSON to get ID for deleting current message
        REPLY_MESSAGE_ID = replyPostResponse.body().jsonPath().get("response.id");
    }

    @AfterMethod
    public final void deletePostedMessage() {

        assertThat(deletePostByID(REPLY_MESSAGE_ID)).isHTTPCode(200);
        assertThat(deletePostByID(POST_MESSAGE_ID)).isHTTPCode(200);
    }
}
