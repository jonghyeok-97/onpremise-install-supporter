package jonghyeok.onpremiseinstallsupporter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import feign.Retryer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients(basePackageClasses = GithubClient.class) // 해당 클래스의 패키지만 스캔
public class FeignClientConfig {
    public static final int FEIGN_RETRY_MAX_ATTEMPTS = 5;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * https://mangkyu.tistory.com/279
     * 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대 5번 시도한다.
     */
    @Bean
    Retryer.Default retryer() {
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), FEIGN_RETRY_MAX_ATTEMPTS);
    }
}
