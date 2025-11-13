package jonghyeok.onpremiseinstallsupporter.scheduler;

import jonghyeok.onpremiseinstallsupporter.DockerImage;
import jonghyeok.onpremiseinstallsupporter.client.GithubClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "docker-sync.enabled", havingValue = "true")
public class DockerImageSyncScheduler {

    private final GithubClient githubClient;
    private final DockerImageStore imageStore;

    @Scheduled(cron = "${docker-sync.schedule.cron}", zone = "Asia/Seoul")
    public void syncImages() {
        log.info("도커 이미지 싱크 스케쥴 시작");
        List<DockerImage> githubImages = githubClient.getAllDockerImages();

        imageStore.store(githubImages);
        log.info("도커 이미지 싱크 스케쥴 종료. 이미지 개수: {}", githubImages.size());
    }
}
