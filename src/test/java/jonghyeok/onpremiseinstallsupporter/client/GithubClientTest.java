package jonghyeok.onpremiseinstallsupporter.client;

import jonghyeok.onpremiseinstallsupporter.DockerImage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles(value = "test")
public class GithubClientTest {
    Logger logger = LoggerFactory.getLogger(GithubClientTest.class);

    @Autowired
    private GithubClient githubClient;

    @Test
    void getAllDockerImagesFromGithub() {
        List<DockerImage> githubImages = githubClient.getAllDockerImages();

        assertThat(githubImages).isNotEmpty();

        logger.debug("이미지 개수 : {}", githubImages.size());
        githubImages.forEach(each -> {
            logger.debug(each.toString());
        });
    }
}
