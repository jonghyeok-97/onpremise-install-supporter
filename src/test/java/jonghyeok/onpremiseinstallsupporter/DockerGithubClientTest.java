package jonghyeok.onpremiseinstallsupporter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DockerGithubClientTest {
    @Autowired
    private DockerGithubClient imageMetaDataClient;

    @Test
    void getAllDockerImagesFromGithub() {
        List<DockerGithubImage> githubImages = imageMetaDataClient.getAllDockerImages();

        assertThat(githubImages).isNotEmpty();
        
        System.out.println("result size : " + githubImages.size());
        githubImages.forEach(each -> {
            System.out.println(each);
            System.out.println();
        });
    }
}
