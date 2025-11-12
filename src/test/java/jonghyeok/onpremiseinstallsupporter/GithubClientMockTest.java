package jonghyeok.onpremiseinstallsupporter;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static jonghyeok.onpremiseinstallsupporter.GlobalFeignClientConfig.FEIGN_RETRY_MAX_ATTEMPTS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    @DisplayName("응답에 Retry-After 헤더가 있으면 최대 3번 시도를 한다.")
    @ValueSource(ints = {403, 429})
    void retryFivesTimesByResponseCode(int responseCode) {
        stubFor(get(GithubClient.DOCKER_REPO_URL)
                .willReturn(aResponse()
                        .withStatus(responseCode)
                        .withHeader(HttpHeaders.RETRY_AFTER, "1")));

        assertThatThrownBy(() -> githubClient.getAllDockerImages())
                .isInstanceOf(RetryableException.class);

        verify(FEIGN_RETRY_MAX_ATTEMPTS, getRequestedFor(urlEqualTo(GithubClient.DOCKER_REPO_URL)));
    }

    @Test
    @DisplayName("전역 FeignClient는 Retry-After 헤더의 값을 엄격하게 지킨다")
    void strictRetry() {
        Duration strictRetryAfterSeconds = Duration.ofSeconds(4);
        stubFor(get(GithubClient.DOCKER_REPO_URL)
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader(HttpHeaders.RETRY_AFTER, String.valueOf(strictRetryAfterSeconds.getSeconds()))));

        long start = System.currentTimeMillis();
        assertThatThrownBy(() -> githubClient.getAllDockerImages())
                .isInstanceOf(RetryableException.class);

        Duration actualTime = Duration.ofMillis(System.currentTimeMillis() - start);
        Duration expectedTime = strictRetryAfterSeconds.multipliedBy(FEIGN_RETRY_MAX_ATTEMPTS - 1);
        assertThat(actualTime).isGreaterThanOrEqualTo(expectedTime);
    }

    @Test
    @DisplayName("IOException 발생 시 설정된 횟수만큼 재시도한다.")
    void retryOnIOException() {
        stubFor(get(GithubClient.DOCKER_REPO_URL)
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatThrownBy(() -> githubClient.getAllDockerImages())
                .isInstanceOf(RetryableException.class)
                .hasCauseInstanceOf(IOException.class);

        /**
         * https://github.com/wiremock/wiremock/issues/1789
         * WireMock 에서 사용중인 ApacheHttpClient 가 IOException 발생 시, 기본적으로 1회 재시도 시도하기 때문에 *2 를 함.
         */
        verify(FEIGN_RETRY_MAX_ATTEMPTS * 2, getRequestedFor(urlEqualTo(GithubClient.DOCKER_REPO_URL)));
    }
}
