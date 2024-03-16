package Mantis;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.cert.CertPath;
import static org.junit.jupiter.api.Assertions.*;

public class RealNameTest {

    private String PHPSESSID;
    private String MANTIS_secure_session;
    private String MANTIS_STRING_COOKIE;
    private Map<String, String> cookies = new HashMap<>();

    @BeforeEach
    public void getCookies() {
        Response responseLogin = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .body("return=%2Fmantisbt%2Fmy_view_page.php&username=admin&password=admin20&secure_session=on").post("https://academ-it.ru/mantisbt/login.php")
                .andReturn();
        PHPSESSID = responseLogin.cookie("PHPSESSID");
        System.out.println("PHPSESSID = " + PHPSESSID);
        MANTIS_secure_session = responseLogin.cookie("MANTIS_secure_session");
        System.out.println("MANTIS_secure_session = " + MANTIS_secure_session);
        MANTIS_STRING_COOKIE = responseLogin.cookie("MANTIS_STRING_COOKIE");
        System.out.println("MANTIS_STRING_COOKIE = " + MANTIS_STRING_COOKIE);
        cookies.put("PHPSESSID", PHPSESSID);
        cookies.put("MANTIS_secure_session", MANTIS_secure_session);
        cookies.put("MANTIS_STRING_COOKIE", MANTIS_STRING_COOKIE);
    }

    @Test
    public void getViewAccountPageTest() {
        Response response = RestAssured
                .given().cookies(cookies)
                .get("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();
        System.out.println("\nResponse:");
        response.prettyPrint();
        assertEquals(200, response.statusCode(), "Response status code is not as expected");
        assertTrue(response.body().asString().contains("Real Name"));
    }

    @Test
    public void updateRealNameTest() {

        String currentTimestamp = String.valueOf(new Timestamp(new Date().getTime()));
        String newRealName = "new_real_name_" + currentTimestamp;

        Response responseUpdateRealName = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies)
                .body("password_current=&password=&password_confirm=&email=rov55an3014@mail.ru&realname=" + newRealName)
                .post("https://academ-it.ru/mantisbt/account_page.php")
                .andReturn();
        System.out.println("\nResponse:");
        responseUpdateRealName.prettyPrint();
        Response responseViewRealName = RestAssured.given().cookies(cookies).get("https://academ-it.ru/mantisbt/view.php?id=" + newRealName).andReturn();
        assertEquals(200, responseViewRealName.statusCode(), "Response status code is not as expected");
    }
}
