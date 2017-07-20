package tests.tests_without_wrapers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import util.DataProvider;
import util.Parser;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static util.APIAssert.assertThat;

public class KernelLoginTest {

    static private final Logger LOGGER = Logger.getLogger(KernelLoginTest.class);

    @Test(dataProvider = "loginData", dataProviderClass = DataProvider.class)
    public void testKernelLogin(final String login, final String pass) throws MalformedURLException, UnsupportedEncodingException {

//        RestAssured.baseURI = "http://digagro-dev.azurewebsites.net";
        RestAssured.useRelaxedHTTPSValidation();

        Response response =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
                        .when()
                        .log().all()
                        .get("http://digagro-dev.azurewebsites.net/cms");

        response.then().log().all();

        assertThat(response).isHTTPCode(302);

        final String firstRedirectLink = response.getHeaders().getValue("Location");//https://52.174.94.197:9449/passivests?wa=wsignin1.0&wtrealm=ServiceProvider&wreply=http://digagro-dev.azurewebsites.net/cms/

        Response secondResponse =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
                        .when()
                        .param("wa", "wsignin1.0")
                        .param("wtrealm", "ServiceProvider")
                        .param("wreply", "http://digagro-dev.azurewebsites.net/cms/")
                        .log().all()
                        .get(firstRedirectLink); //https://52.174.94.197:9449/passivests?wa=wsignin1.0&wtrealm=ServiceProvider&wreply=http://digagro-dev.azurewebsites.net/cms/

        secondResponse.then().log().all();

        assertThat(secondResponse).isHTTPCode(302);

        final String secondRedirectLink = secondResponse.getHeaders().getValue("Location"); //https://52.174.94.197:9449/commonauth?sessionDataKey=18d1ad29-0bcc-4ece-8c7f-3e7c770abef7&type=passivests

        String sessionDataKey = secondRedirectLink.split("sessionDataKey=")[1].split("&")[0];

        Response thirdResponse =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
                        .when()
                        .param("sessionDataKey", sessionDataKey)
                        .param("type", "passivests")
                        .log().all()
                        .get(secondRedirectLink); //https://52.174.94.197:9449/commonauth?sessionDataKey=82afe7bc-74ee-417b-8fcd-241522f5173e&type=passivests

        thirdResponse.then().log().all();

        assertThat(thirdResponse).isHTTPCode(302);

        String thirdRedirectLink = thirdResponse.getHeaders().getValue("Location"); //https://52.174.94.197:9449/authenticationendpoint/login.do?commonAuthCallerPath=%2Fpassivests&forceAuth=false&passiveAuth=false&wa=wsignin1.0&wreply=http%3A%2F%2Fdigagro-dev.azurewebsites.net%2Fcms%2F&wtrealm=ServiceProvider&sessionDataKey=d2ac4bdb-9a54-4e8a-9837-83f5d887fac6&relyingParty=ServiceProvider&type=passivests&sp=ServiceProvider&isSaaSApp=false&authenticators=BasicAuthenticator:LOCAL

        String firstDataKey = thirdRedirectLink.split("sessionDataKey=")[1].split("&")[0];

        //parse locationQueryMap
        final Parser parser = new Parser();

        Map<String, Object> locationQueryMap = parser.parseQueryMapFromQueryString(thirdRedirectLink);

        Response authenticationEndPointResponse =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
                        .when()
                        .params(locationQueryMap)
                        .log().all()
                        .get(thirdRedirectLink); //https://52.174.94.197:9449/authenticationendpoint/login.do?commonAuthCallerPath=%2Fpassivests&forceAuth=false&passiveAuth=false&wa=wsignin1.0&wreply=http%3A%2F%2Fdigagro-dev.azurewebsites.net%2Fcms%2F&wtrealm=ServiceProvider&sessionDataKey=d2ac4bdb-9a54-4e8a-9837-83f5d887fac6&relyingParty=ServiceProvider&type=passivests&sp=ServiceProvider&isSaaSApp=false&authenticators=BasicAuthenticator:LOCAL

        authenticationEndPointResponse.then().log().all();

        assertThat(authenticationEndPointResponse).isHTTPCode(200);

        Response loginResponse =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .body("sessionDataKey=" + firstDataKey + "&username=" + login + "&password=" + pass)
                        .baseUri("https://52.174.94.197:9449")
                        .basePath("/commonauth")
                        .when()
                        .log().all()
                        .post(); //https://52.174.94.197:9449/commonauth

        loginResponse.then().log().all();

        assertThat(loginResponse).isHTTPCode(302);

        String firthRedirectLink = loginResponse.getHeaders().getValue("Location");

        String thirdSessionDataKey = firthRedirectLink.split("sessionDataKey=")[1].split("&")[0];

        Response passiveSTSvsSessionDataKeyResponse =
                given()
                        .redirects().follow(false)
                        .accept(ContentType.JSON)
//                        .baseUri("https://52.174.94.197:9449")
//                        .basePath("/passivests")
                        .when()
                        .params("sessionDataKey", thirdSessionDataKey)
                        .log().all()
                        .get(firthRedirectLink); //https://52.174.191.33:9449/passivests?sessionDataKey=b54c7695-24a4-48e8-a40b-6ac4218a32f3

        passiveSTSvsSessionDataKeyResponse.then().log().all();

        assertThat(passiveSTSvsSessionDataKeyResponse).isHTTPCode(200);

        String wresult = passiveSTSvsSessionDataKeyResponse.body().asString().split("name=\"wresult\" value=\"")[1].split("\">")[0].replaceAll("&lt;", "<").replaceAll("&#34;", "\"");


        Response lastResponse =
                given()
                        .redirects().follow(true)
                        .accept(ContentType.JSON)
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .body("wa=wsignin1.0&wresult=" + parser.string2url(wresult))
                        .baseUri("http://digagro-dev.azurewebsites.net")
                        .basePath("/cms")
                        .when()
                        .log().all()
                        .post();

        lastResponse.then().log().all();

        assertThat(lastResponse).isHTTPCode(302);

        Map<String, String> map = lastResponse.cookies();

        Response lastGETcms =
                given()
                        .redirects().follow(false)
                        .baseUri("http://digagro-dev.azurewebsites.net")
                        .basePath("/cms")
                        .cookies(map)
                        .when()
                        .log().all()
                        .get();

        lastGETcms.then().log().all();

        assertThat(lastGETcms).isHTTPCode(200);


//        Map<String, String> map1 = lastResponse.cookies();
//
//        Response loginAssert =
//                given()
//                        .redirects().follow(false)
//                        .baseUri("http://dev-dab.azurewebsites.net")
//                        .basePath("/Security.Api/api/users/current")
//                        .cookies(".ASPXAUTH", "2226830F00EF0275FFB445BAD8DFF662F05625855BB195588F6A729492D629A75D8F2D7B5A8B96C91B934796E78715133F5DEBC55A05B8F53B03A090053F7AF763E1BD98BBF3068C3047235DAE97A0238CE8BDB01BCF4E4FCB11D00431E5310720EF55B366DC16FF8241C3CC9D1AA5C8; expires=Thu, 09-Mar-2017 15:24:04 GMT; path=/; HttpOnly")
//                        .cookies("CMSPreferredUICulture", "uk-UA; expires=Fri, 09-Mar-2018 14:54:04 GMT; path=/; HttpOnly")
//                        .cookies("FedAuth", "77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U2VjdXJpdHlDb250ZXh0VG9rZW4gcDE6SWQ9Il8yYmNmMjdlMC0zZjZmLTQxZDItYTVkZC1lOTliNjM2YWIwMDEtQkFBQTUyMDdFNTlEQzZGRkY4REQ5OEIyMTg5Mzk4RUYiIHhtbG5zOnAxPSJodHRwOi8vZG9jcy5vYXNpcy1vcGVuLm9yZy93c3MvMjAwNC8wMS9vYXNpcy0yMDA0MDEtd3NzLXdzc2VjdXJpdHktdXRpbGl0eS0xLjAueHNkIiB4bWxucz0iaHR0cDovL2RvY3Mub2FzaXMtb3Blbi5vcmcvd3Mtc3gvd3Mtc2VjdXJlY29udmVyc2F0aW9uLzIwMDUxMiI+PElkZW50aWZpZXI+dXJuOnV1aWQ6MDU1OTU4YTUtM2Y4Yy00ZTk0LTg4OTMtZDVjMDc1MjQ0NmUxPC9JZGVudGlmaWVyPjxDb29raWUgeG1sbnM9Imh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwNi8wNS9zZWN1cml0eSI+cE42WXRLdk1pZWdHajgrbTZVT0hiODd5dEJ2TTFNVloxZ1dDeWNhdEZuQmtEZ2xBLytFNE42UVZTbkhnSzA0bVhtczJ0QlZtVDNzVDd3ZmtnalJhNDFZWUd6T3R1aDNaMnhOa3YwWG8vM3ZPaTlsWmtkUmF3b1ZpM2ZTVTJoVklXaFZOdkhuYWl1NzBHNUdWTGY4SmFVOXdkdjRMaHdlVTA1SThIcE5sZDNKeHpxMTEzNHdxVlF4c0dWOHh0MTM5QzhKN1RJV0srRmQwQUQzZVkvaVlMTG56R3RyZzlna2g3dC80b3FndnJaV2VlS2JiWnE4QmhOZ3VHU1FTaTFVa1dhaDhnVjZLQ05zejJXcTVydXVTYnpjNHc4aG1mdk9Bay9MVkdxVFJxNmdMWXJKbTZVL3RRWlBUQlkyUjdIeFZzdDZNVlcvUk5jRkQwY3lxYjZLY2d1bUZjT1YyOGU1ZkZLdzBFMU5zUzJqKzk3Z09rUS91OVZraVYvY0I2MEYvbStzMVltdHBrMmMrekVweHFPWnljQ1NMZmpaK056dms1ck5mY2t5R1dwei91UXFoL3pPbFBKbjVkcXR1b1pzc2FiN2pUWXQ0Um9uSFk1SUM3RUkrNkNkUzBhVS9hbVRpaFdKRzNMUU1NTElnbnorZDF2a2ZNY2k4RUt0QnY4elpSOFh2VzBObkdFY1IvaXpqWVlLelJwUksvSjRGRWkxRzB0RjNpdDBseGJkNlpyR3owR1RORVQvMHRlVVFPNmhLM2JEWXFqdy9TYVhidXpYRUFXZVVSTXZpU1hqcDdpZUUwcUpIbklHNDZxY0FIUnVldHAzN0VwM1VYbWdjSEJmZjdFUHR4RERWZzRnWnBNdmZiaTc4WVpiblFZL2Z5Z2NMTFpmQk1DbWhmb2dwYTJNWE5aZlR4VmR2UXpqYU0ycm1ZVjk1T1BvejhNbzc5NkdvcGo2aWJ6OEdIK1MwZDlKd0lBYndIc3VWM091M2txS2xyVmdGcE5RMXN4UVl3NjlrZEdYS0JLdXJtQUV1R20rKzliZmRkMnQzd2tBdEN5d3ZwK0NIYk9VNis3ZnBlakZMbi8zb2pIb1RrOTNPRWFMNm9tQ21zenkwQlJyZnZxZUNyeXBkUXlpTG5IdXM1Rm9MZy9QS1Vod3hYblNkVW1yMWtPKzhqZUlaeVdSQ2taeitaYUpHZGVPMnZQVGcwOXJLUS9LTVJsSHpySXZOUHRzd0Z2YTJBaytWNGF6QXhZcEVUUFAweTNQV0VzRjN4VTFLQ3VlUm12T2tRQlpBdHNEZE9JMVpGZE5ZM0p4VWVoSTh2b3Z3anRrRjZhbEZSODJXSmp4SDlHNGJyanhCT1Z2VjZzRnhUUG9JbHN0ZkQ5SE1hZ0Z6aHpka0MyNG8wUm5PazJu; expires=Thu, 09-Mar-2017 15:54:04 GMT; path=/; HttpOnly")
//                        .cookies("FedAuth1", "QVRDeHhTNHlQazNpK2RHR2dGSGowQThTQjAxaFhDTlltaVp1dVBLRldZYVh0c3F5NTZQK2l0bE05V1drU01ZZTVWR2pQY3FVPTwvQ29va2llPjwvU2VjdXJpdHlDb250ZXh0VG9rZW4+; expires=Thu, 09-Mar-2017 15:54:04 GMT; path=/; HttpOnly")
////                        .cookies(map)
//                        .when()
//                        .log().all()
//                        .get();
//        loginAssert.then().log().all();
//
//        assertThat(loginAssert).isHTTPCode(200);

    }
}
