package jonghyeok.onpremiseinstallsupporter.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import feign.Retryer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients
public class GlobalFeignClientConfig {
    public static final int FEIGN_RETRY_MAX_ATTEMPTS = 3;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * 1. Retry-After 헤더 존재 시, 해당 헤더 시간을 엄격하게 존중
     * 2. Retry-After 헤더 없을 시, 0.1초의 간격으로 시작해 최대 3초의 간격으로 점점 증가하며, 최대 3번 시도한다.
     */
    @Bean
    Retryer retryer() {
        return new StrictRetryer(100L, TimeUnit.SECONDS.toMillis(3L), FEIGN_RETRY_MAX_ATTEMPTS);
    }
}
