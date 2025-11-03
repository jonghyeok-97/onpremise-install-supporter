package jonghyeok.onpremiseinstallsupporter;

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

        List<DockerGithubImage> githubImages = List.of(
                new DockerGithubImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Tomcat", "path", "", "", "", "", "", "", "", null)
        );

        assertThatThrownBy(() -> localFileStore.store(githubImages))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void storeFailWhenPathIsBlank(String filePath) {
        LocalFileStore localFileStore = new LocalFileStore(filePath);

        List<DockerGithubImage> githubImages = List.of(
                new DockerGithubImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Tomcat", "path", "", "", "", "", "", "", "", null)
        );

        assertThatThrownBy(() -> localFileStore.store(githubImages))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void storeImageNames() throws IOException {
        LocalFileStore localFileStore = new LocalFileStore(filePath.toString());

        List<DockerGithubImage> githubImages = List.of(
                new DockerGithubImage("Nginx", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Redis", "path", "", "", "", "", "", "", "", null),
                new DockerGithubImage("Tomcats", "path", "", "", "", "", "", "", "", null)
        );

        localFileStore.store(githubImages);

        assertThat(Files.readString(filePath)).isEqualTo("name=Nginx,Redis,Tomcats");
        assertThat(filePath).exists();
    }
}