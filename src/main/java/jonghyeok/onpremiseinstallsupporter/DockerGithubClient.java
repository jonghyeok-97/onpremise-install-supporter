package jonghyeok.onpremiseinstallsupporter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Docker Github 의 Docker Image 목록에 접근합니다.
 */
@FeignClient(name = "dockerHubFeignClient", url = DockerGithubClient.DOCKER_IMAGE_INFO_URL)
public interface DockerGithubClient {

    String DOCKER_IMAGE_INFO_URL = "https://api.github.com/repos/docker-library/official-images/contents/library";

    @GetMapping(headers = "Accept=application/vnd.github.v3+json")
    List<DockerGithubImage> getAllDockerImages();
}