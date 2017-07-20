package util;

public class DataProvider {

    @org.testng.annotations.DataProvider
    public static Object[][] loginData() {
        return new Object[][]{
                {"Leonid.Garchuk", "123456"}
        };
    }
}
