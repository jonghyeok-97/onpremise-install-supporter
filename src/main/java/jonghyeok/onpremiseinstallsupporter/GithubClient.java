package jonghyeok.onpremiseinstallsupporter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Docker Github 의 Docker Image 목록에 접근합니다.
 */
@FeignClient(name = "dockerHubFeignClient", url = "${github.url}")
public interface GithubClient {

    @GetMapping(path = "/repos/docker-library/official-images/contents/library", headers = "Accept=application/vnd.github.v3+json")
    List<DockerImage> getAllDockerImages();
}