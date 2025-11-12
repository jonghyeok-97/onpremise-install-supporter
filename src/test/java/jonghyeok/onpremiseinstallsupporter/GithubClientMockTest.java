package jonghyeok.onpremiseinstallsupporter;

import com.github.tomakehurst.wiremock.client.WireMock;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureWireMock(port = GithubClientMockTest.RANDOM_PORT)
@TestPropertySource(properties = "github.url=http://localhost:${wiremock.server.port}")
public class GithubClientMockTest {
    static final int RANDOM_PORT = 0;

    @Autowired
    GithubClient githubClient;

    @BeforeEach
    void setUp() {
        WireMock.resetAllRequests();
    }

    @ParameterizedTest
    @DisplayName("429(TOO_MANY_REQUESTS), 503(SERVICE_UNAVAILABLE) 응답에 Retry-After 헤더가 있으면 최대 5번 시도를 한다.")
    @ValueSource(ints = {429, 503})
    void retryFivesTimesByResponseCode(int responseCode) {
        stubFor(get(GithubClient.DOCKER_REPO_URL)
                .willReturn(aResponse()
                        .withStatus(responseCode)
                        .withHeader(HttpHeaders.RETRY_AFTER, "1")));

        assertThatThrownBy(() -> githubClient.getAllDockerImages())
                .isInstanceOf(RetryableException.class);

        verify(FeignClientConfig.FEIGN_RETRY_MAX_ATTEMPTS, getRequestedFor(urlEqualTo(GithubClient.DOCKER_REPO_URL)));
    }
}
