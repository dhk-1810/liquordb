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
class LiquorLikeTest {

    public static final String BASE_URL = "http://localhost:8080"
    public static final Long TARGET_LIQUOR_ID = 1L // 테스트 대상 주류 ID
    public static final String AUTH_TOKEN = "Bearer YOUR_JWT_TOKEN_HERE"

    public static GTest testLikeLiquor = new GTest(1, "POST /api/liquors/{id}/like")
    public static HTTPRequest request = new HTTPRequest()

    @BeforeProcess
    public static void beforeProcess() {
        testLikeLiquor.record(request, "POST")
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true
    }

    @Test
    public void test() {
        NVPair[] headers = [
            new NVPair("Authorization", AUTH_TOKEN),
            new NVPair("Content-Type", "application/json")
        ] as NVPair[]

        HTTPResponse response = request.POST(
            BASE_URL + "/api/liquors/" + TARGET_LIQUOR_ID + "/like",
            new byte[0],
            headers
        )

        // 201 Created 또는 중복 좋아요 시 409/400 처리
        assertThat(response.statusCode, anyOf(is(201), is(409), is(400)))
    }
}
