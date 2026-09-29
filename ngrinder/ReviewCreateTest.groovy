import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.script.GTest
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import net.grinder.plugin.http.HTTPRequest
import HTTPClient.HTTPResponse
import HTTPClient.NVPair
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GrinderRunner)
class ReviewCreateTest {

    public static final String BASE_URL = "http://localhost:8080"
    public static final Long TARGET_LIQUOR_ID = 1L // 동시 경합을 유도할 주류 ID
    public static final String AUTH_TOKEN = "Bearer YOUR_JWT_TOKEN_HERE"

    public static GTest testCreateReview = new GTest(1, "POST /api/liquors/{id}/reviews")
    public static HTTPRequest request = new HTTPRequest()

    @BeforeProcess
    public static void beforeProcess() {
        testCreateReview.record(request, "POST")
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true
    }

    @Test
    public void test() {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
        NVPair[] headers = [
            new NVPair("Authorization", AUTH_TOKEN),
            new NVPair("Content-Type", "multipart/form-data; boundary=" + boundary)
        ] as NVPair[]

        // ReviewRequest JSON Body 구성
        String reviewJson = """{
            "rating": 5,
            "title": "단일 부하 테스트 리뷰",
            "content": "동시성 및 낙관적 락 경합 테스트용 본문입니다.",
            "tags": ["테스트", "위스키"]
        }"""

        String body = "--" + boundary + "\r\n" +
                      "Content-Disposition: form-data; name=\"request\"\r\n" +
                      "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
                      reviewJson + "\r\n" +
                      "--" + boundary + "--\r\n"

        HTTPResponse response = request.POST(
            BASE_URL + "/api/liquors/" + TARGET_LIQUOR_ID + "/reviews",
            body.getBytes("UTF-8"),
            headers
        )

        // 성공(200) 또는 낙관적 락 충돌(409/500) 상태코드 검증
        assertThat(response.statusCode, anyOf(is(200), is(409), is(500)))
    }
}
