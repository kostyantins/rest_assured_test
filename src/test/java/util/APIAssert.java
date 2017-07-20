package util;

import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class APIAssert {

    private Response actual;

    private APIAssert(final Response actual) {
        this.actual = actual;
    }

    public static final APIAssert assertThat(final Response actual) {
        return new APIAssert(actual);
    }

    public final APIAssert isHTTPCode(final int statusCode) {

        isNotNull();

        assertEquals(actual.getStatusCode(), statusCode, "HTTP status code fail\n" + actual.toString() + "\n");

        return this;
    }

    private final APIAssert isNotNull() {

        assertNotNull(actual.getBody(), "Response body == null\n");

        return this;
    }
}
