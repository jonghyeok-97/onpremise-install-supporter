package jonghyeok.onpremiseinstallsupporter.client;

import jonghyeok.onpremiseinstallsupporter.DockerImage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Docker Github 의 Docker Image 목록에 접근합니다.
 */
@FeignClient(name = "dockerHubFeignClient", url = "${docker-sync.github.url}")
public interface GithubClient {

    String DOCKER_REPO_URL = "/repos/docker-library/official-images/contents/library";

    @GetMapping(path = DOCKER_REPO_URL, headers = "Accept=application/vnd.github.v3+json")
    List<DockerImage> getAllDockerImages();
}