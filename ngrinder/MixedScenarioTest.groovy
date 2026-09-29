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
import java.util.concurrent.ThreadLocalRandom

@RunWith(GrinderRunner)
class MixedScenarioTest {

    // 1. 엔드포인트 및 타겟 서버 설정
    public static final String BASE_URL = "http://localhost:8080" // 테스트 대상 서버 주소
    public static final Long TARGET_LIQUOR_ID = 1L              // 동시 경합을 유도할 주류 ID
    public static final String AUTH_TOKEN = "Bearer YOUR_JWT_TOKEN_HERE" // 인증 토큰 (필요시)

    // 2. nGrinder 통계에서 API별로 TPS/응답시간을 분리 집계하기 위한 GTest 정의
    public static GTest testListLiquors = new GTest(1, "GET /api/liquors (80%)")
    public static GTest testLikeLiquor = new GTest(2, "POST /api/liquors/{id}/like (15%)")
    public static GTest testCreateReview = new GTest(3, "POST /api/liquors/{id}/reviews (5%)")

    public static HTTPRequest request = new HTTPRequest()

    @BeforeProcess
    public static void beforeProcess() {
        // GTest 계측(Instrument) 등록
        testListLiquors.record(request, "GET")
        testLikeLiquor.record(request, "POST")
        testCreateReview.record(request, "POST")
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true
    }

    @Test
    public void test() {
        // 1 ~ 100 난수를 통한 비율 분기 (80% / 15% / 5%)
        int randomRatio = ThreadLocalRandom.current().nextInt(1, 101)

        if (randomRatio <= 80) {
            // 80%: 주류 목록 다건 조회
            getLiquors()
        } else if (randomRatio <= 95) {
            // 15%: 주류 좋아요 (낙관적/Row Lock 및 Redis 경합)
            likeLiquor(TARGET_LIQUOR_ID)
        } else {
            // 5%: 리뷰 작성 (낙관적 락 충돌 및 평점 갱신 경합)
            createReview(TARGET_LIQUOR_ID)
        }
    }

    /**
     * 80%: 주류 다건 조회 (커서 페이징)
     */
    private void getLiquors() {
        NVPair[] headers = [
            new NVPair("Authorization", AUTH_TOKEN)
        ] as NVPair[]
        NVPair[] params = [
            new NVPair("limit", "20"),
            new NVPair("sortBy", "LIQUOR_ID"),
            new NVPair("sortDirection", "DESC")
        ] as NVPair[]

        HTTPResponse response = request.GET(BASE_URL + "/api/liquors", params, headers)
        assertThat(response.statusCode, is(200))
    }

    /**
     * 15%: 주류 좋아요 등록
     */
    private void likeLiquor(Long liquorId) {
        NVPair[] headers = [
            new NVPair("Authorization", AUTH_TOKEN),
            new NVPair("Content-Type", "application/json")
        ] as NVPair[]

        HTTPResponse response = request.POST(BASE_URL + "/api/liquors/" + liquorId + "/like", new byte[0], headers)
        // 201 Created 또는 중복 좋아요 시의 상태코드에 맞춰 검증
        assertThat(response.statusCode, anyOf(is(201), is(409), is(400)))
    }

    /**
     * 5%: 리뷰 등록 (Multipart Form-Data)
     */
    private void createReview(Long liquorId) {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
        NVPair[] headers = [
            new NVPair("Authorization", AUTH_TOKEN),
            new NVPair("Content-Type", "multipart/form-data; boundary=" + boundary)
        ] as NVPair[]

        // ReviewRequest JSON Body 구성
        String reviewJson = """{
            "rating": 5,
            "title": "부하 테스트 리뷰",
            "content": "nGrinder 동시성 테스트용 본문입니다.",
            "tags": ["테스트", "위스키"]
        }"""

        // multipart/form-data의 'request' part 페이로드 생성
        String body = "--" + boundary + "\r\n" +
                      "Content-Disposition: form-data; name=\"request\"\r\n" +
                      "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
                      reviewJson + "\r\n" +
                      "--" + boundary + "--\r\n"

        HTTPResponse response = request.POST(
            BASE_URL + "/api/liquors/" + liquorId + "/reviews",
            body.getBytes("UTF-8"),
            headers
        )

        // 낙관적 락 충돌 시 409/500 또는 성공(200) 상태코드 허용
        assertThat(response.statusCode, anyOf(is(200), is(409), is(500)))
    }
}
