package tests.tests_without_wrapers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import util.DataProvider;

import java.net.MalformedURLException;

import static io.restassured.RestAssured.given;
import static util.APIAssert.assertThat;
import static util.BaseAPIData.*;

public class KernelLogin02Test {

    @Test(dataProvider = "loginData", dataProviderClass = DataProvider.class)
    public void testKernelLogin(final String login, final String pass) throws MalformedURLException {

        // to eliminate warnings (like SSL protocol)
        RestAssured.useRelaxedHTTPSValidation();

       final String sessionDataKey = "49465512-536b-4532-9fa3-e2eea0d4cf91";

        Response response =
                given()
                        .accept(ContentType.JSON)
                        .body("sessionDataKey=" + sessionDataKey + "&username=" + login + "&password=" + pass)
                        .baseUri("https://52.174.94.197:9449")
                        .basePath("/commonauth")
                        .when().log().all()
                        .post();

        assertThat(response).isHTTPCode(200);
    }
}
