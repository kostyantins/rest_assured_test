package tests.tests_with_wrapers;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import util.APITestRunner;

import static util.APIAssert.assertThat;
import static util.BaseAPIData.DISCUSS_URL;

public class GetDiscussTest extends APITestRunner {

    @Test
    public final void testEditPost() {

        assertThat(getResource(DISCUSS_URL)).isHTTPCode(200);
    }
}
