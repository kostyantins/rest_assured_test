package util;

import io.restassured.response.Response;
import jdk.nashorn.internal.parser.JSONParser;
import org.testng.log4testng.Logger;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    private final Logger LOGGER = Logger.getLogger(this.getClass());

    public Map<String, Object> parseQueryMapFromQueryString(String query) {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        //commonAuthCallerPath=%2Fpassivests&forceAuth=false&passiveAuth=false&wa=wsignin1.0&wreply=http%3A%2F%2Fdev-dab.azurewebsites.net%2Fcms%2F&wtrealm=ServiceProvider&sessionDataKey=8b9e38c5-2d99-4bd2-a466-bd03ce8d9f66&relyingParty=ServiceProvider&type=passivests&sp=ServiceProvider&isSaaSApp=false&authenticators=BasicAuthenticator:LOCAL
        if (query != null) {
            for (String queryEntry : query.split("&")) {
                try {
                    String key = queryEntry.split("=")[0];
                    String value = queryEntry.split("=")[1];
                    queryMap.put(key, value);
                } catch (Exception e) {
                    LOGGER.warn("Query parsing error query : " + query);
                    e.printStackTrace();
                }
            }
        }
        return queryMap;
    }

//    public String parseLocationPathFromHeader(Response responseModel) throws MalformedURLException {
//        String location = parseLocationFromHeader(responseModel);
//        if (location != null) {
//            try {
//                return new URL(location).getPath();
//            } catch (Exception e) {
//                e.printStackTrace();
//                LOGGER.warn("Can't parse path from '" + location + "'");
//            }
//        }
//        LOGGER.warn("Location = null");
//        return null;
//    }

//    public String parseWresultFromHTMLInput(Response responseModel) {
//        String res = null;
//        try {
//            //spike parsing because of RestAssured distorted original body
//            res = responseModel.body().split("name=\"wresult\" value=\"")[1].split("\">")[0].replaceAll("&lt;", "<").replaceAll("&#34;", "\"");
//        } catch (Exception e) {
//            LOGGER.warn("Can't parse wresult from : '" + responseModel.getBody() + "'");
//            e.printStackTrace();
//        }
//        return res;
//    }

//    public JSONObject getBodyAsJson(Response response) {
//        JSONObject res = null;
//        try {
//            res = (JSONObject) new JSONParser().parse(response.getBody());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return res;
//    }

    public static String string2url(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }
}
