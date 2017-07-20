package tests;

import org.testng.annotations.Test;

public class SubstringTest {

    @Test
    public void testSubS() {

        String string = "https://52.174.94.197:9449/passivests?wa=wsignin1.0&wtrealm=ServiceProvider&wreply=http://dev-dab.azurewebsites.net/cms/";

        string = string.substring(0, string.indexOf(":"));

        System.out.println(string);
    }

    @Test
    public void testSubSS() {

        String string = "{\"result\":true,\"exception\":null,\"asyncState\":null,\"isFaulted\":false,\"isCanceled\":false,\"creationOptions\":8,\"id\":3636,\"status\":5,\"isCompleted\":true}";

        String bodyId = string.substring(string.indexOf("id") + 4 , string.indexOf("status") -2);

        System.out.println(bodyId);
    }
}
