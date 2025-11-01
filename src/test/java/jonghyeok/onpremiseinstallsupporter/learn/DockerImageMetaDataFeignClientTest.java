package jonghyeok.onpremiseinstallsupporter.learn;

import jonghyeok.onpremiseinstallsupporter.DockerImageMetaData;
import jonghyeok.onpremiseinstallsupporter.DockerImageMetaDataFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DockerImageMetaDataFeignClientTest {
    @Autowired
    private DockerImageMetaDataFeignClient dockerImageMetaDataFeignClient;

    @Test
    void getDockerImagesMetaDataFromDockerGithub() {
        List<DockerImageMetaData> dockerImage = dockerImageMetaDataFeignClient.getDockerImage();
        System.out.println("result size : "  + dockerImage.size());
        dockerImage.forEach(each -> {
            System.out.println(each);
            System.out.println();
        });
    }
}
