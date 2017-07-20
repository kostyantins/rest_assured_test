package util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeMethod;

public class APITestRunner extends AbstractBaseTest {

    @BeforeMethod
    public final void setUp() {

        RestAssured.baseURI = "https://disqus.com";
    }
}
