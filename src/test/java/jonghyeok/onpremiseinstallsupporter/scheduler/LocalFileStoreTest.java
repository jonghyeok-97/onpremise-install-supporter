package jonghyeok.onpremiseinstallsupporter.scheduler;

import jonghyeok.onpremiseinstallsupporter.DockerImage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalFileStoreTest {

    private Path filePath;

    @BeforeEach
    void setUp() throws IOException {
        filePath = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "images", "image.txt");
        Files.createDirectories(filePath.getParent());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(filePath);
        Files.deleteIfExists(filePath.getParent());
    }

    @Test
    void storeFailWhenPathIsNull() {
        LocalFileStore localFileStore = new LocalFileStore(null);

        List<DockerImage> githubImages = List.of(
                new DockerImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Tomcat", "path", "", "", "", "", "", "", "", null)
        );

        assertThatThrownBy(() -> localFileStore.store(githubImages))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void storeFailWhenPathIsBlank(String filePath) {
        LocalFileStore localFileStore = new LocalFileStore(filePath);

        List<DockerImage> githubImages = List.of(
                new DockerImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Tomcat", "path", "", "", "", "", "", "", "", null)
        );

        assertThatThrownBy(() -> localFileStore.store(githubImages))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void storeImageNames() throws IOException {
        LocalFileStore localFileStore = new LocalFileStore(filePath.toString());

        List<DockerImage> githubImages = List.of(
                new DockerImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerImage("Tomcats", "path", "", "", "", "", "", "", "", null)
        );

        localFileStore.store(githubImages);

        assertThat(Files.readString(filePath)).isEqualTo("name=Nginx,Redis,Tomcats");
        assertThat(filePath).exists();
    }
}