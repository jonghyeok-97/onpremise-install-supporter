package jonghyeok.onpremiseinstallsupporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LocalFileStore implements DockerImageStore {

    private final String filePath;

    public LocalFileStore(@Value("${image.path.file}") String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void store(List<DockerGithubImage> images) throws IOException {
        if(!StringUtils.hasText(filePath)) {
            throw new IllegalArgumentException("image.path.file should has text");
        }

        List<String> imageNames = images.stream()
                .map(DockerGithubImage::name)
                .toList();

        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        try (OutputStream os = fileSystemResource.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))
        ) {
            writer.write("name=" + String.join(",", imageNames));
        }
    }
}
