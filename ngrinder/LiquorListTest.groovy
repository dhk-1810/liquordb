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
class LiquorListTest {

    public static final String BASE_URL = "http://host.docker.internal:8080"

    public static GTest testListLiquors = new GTest(1, "GET /api/liquors")
    public static HTTPRequest request = new HTTPRequest()

    @BeforeProcess
    public static void beforeProcess() {
        testListLiquors.record(request, "GET")
    }

    @BeforeThread
    public void beforeThread() {
        grinder.statistics.delayReports = true
    }

    @Test
    public void test() {
        NVPair[] params = [
                new NVPair("limit", "20"),
                new NVPair("sortBy", "LIQUOR_ID"),
                new NVPair("sortDirection", "DESC")
        ] as NVPair[]

        HTTPResponse response = request.GET(BASE_URL + "/api/liquors", params)
        assertThat(response.statusCode, is(200))
    }
}
