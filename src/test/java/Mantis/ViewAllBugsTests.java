package Mantis;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewAllBugsTests {
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
    public void getViewAllBugsPageTest() {
        Response response = RestAssured
                .given().cookies(cookies)
                .get("https://academ-it.ru/mantisbt/view_all_bug_page.php")
                .andReturn();
        System.out.println("\nResponse:");
        response.prettyPrint();
        assertEquals(200, response.statusCode(), "Response status code is not as expected");
        assertTrue(response.body().asString().contains("Viewing Issues"));
    }

    @Test
    public void updateBugStatusTest() {
        String bugId = "34524";
        Response responseUpdateBug = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies).body("bug_id=" + bugId + "&last_updated=1536986794&category_id=1&view_state=10&handler_id=0&priority=30&severity=50&reproducibility=70&status=50&resolution=10&platform=&os=&os_build=&summary=%D1%82%D0%B5%D0%BC%D0%B0&description=%D0%BE%D0%BF%D0%B8%D1%81%D0%B0%D0%BD%D0%B8%D0%B5&steps_to_reproduce=&additional_information=&bugnote_text=").post("https://academ-it.ru/mantisbt/bug_update.php")
                .andReturn();
        System.out.println("\nResponse:");
        responseUpdateBug.prettyPrint();
        assertEquals(302, responseUpdateBug.statusCode(), "Response status code is not as expected");
        Response responseViewBug = RestAssured.given().cookies(cookies).get("https://academ-it.ru/mantisbt/view.php?id=" + bugId).andReturn();
        assertEquals(200, responseViewBug.statusCode(), "Response status code is not as expected");
        assertTrue(responseViewBug.body().asString().contains("Status</th><td class=\"bug-status\"><i class=\"fa fa-square fa-status-box status-50-color\"></i> assigned"));
    }

    @Test
    public void reportIssueTest() {
        String summary = "Autotest API summary - 2";
        String description = "Autotest API description - 2";
        Response responseCreateBug = RestAssured
                .given()
                .contentType("application/x-www-form-urlencoded")
                .cookies(cookies)
                .body("project_id=1&category_id=1&summary=" + summary + "&description=" + description)
                .post("https://academ-it.ru/mantisbt/bug_report.php?posted=1")
                .andReturn();
        System.out.println("\nResponse:");
        responseCreateBug.prettyPrint();
        assertEquals(200, responseCreateBug.statusCode(), "Response status code is not as expected");
        assertFalse(responseCreateBug.body().toString().contains("APPLICATION ERROR"));
    }
}

